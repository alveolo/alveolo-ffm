package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.RELEASE_25;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.ForeignUnion;
import org.alveolo.ffm.Sequence;

@SupportedAnnotationTypes({
  "org.alveolo.ffm.ForeignStruct",
  "org.alveolo.ffm.ForeignUnion",
})
@SupportedSourceVersion(RELEASE_25)
public class ForeignMemoryProcessor extends AbstractProcessor {
  private static final Set<String> NIO_BUFFER_TYPES = Set.of(
      "java.nio.ByteBuffer", "java.nio.ShortBuffer",
      "java.nio.CharBuffer",
      "java.nio.IntBuffer", "java.nio.LongBuffer",
      "java.nio.FloatBuffer", "java.nio.DoubleBuffer");

  /// Represents a struct field inferred from accessor methods.
  static record StructField(
      String name, TypeMirror typeMirror, long sequence, Element errorElement
  ) {}

  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var messager = processingEnv.getMessager();

    if (roundEnv.processingOver()) return true;

    for (var annotation : annotations) {
      for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element instanceof TypeElement type) {
          switch (type.getKind()) {
            case INTERFACE:
            case RECORD:
              try {
                if (type.getAnnotation(ForeignStruct.class) != null) {
                  writeFile(type, "structLayout");
                }
                if (type.getAnnotation(ForeignUnion.class) != null) {
                  if (type.getKind() == ElementKind.RECORD) {
                    messager.printError("@" + annotation.getSimpleName()
                        + " can only be applied to an interface, not "
                        + ElementKind.RECORD, type);
                  } else {
                    writeFile(type, "unionLayout");
                  }
                }
              } catch (Throwable e) {
                var sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                messager.printError(sw.toString(), type);
              }
              break;
            case ElementKind kind:
              messager.printError("@"
                  + annotation.getSimpleName()
                  + " can only be applied to an interface, not " + kind, type);
          }
        }
      }
    }

    return true;
  }

  /// Infer struct fields from interface accessor methods.
  private List<StructField> inferFields(TypeElement iface) {
    if (iface.getKind() == ElementKind.RECORD) {
      var rcGens = iface.getRecordComponents().stream()
          .map(rc -> new VariableGenerator(processingEnv, rc))
          .toList();

      return rcGens.stream()
          .map(v -> new StructField(v.name,
              v.typeMirror, v.sequence, v.element))
          .toList();
    }

    // Group methods by name
    Map<String, List<ExecutableElement>> methodsByName = new LinkedHashMap<>();
    for (var enc : iface.getEnclosedElements()) {
      if (enc instanceof ExecutableElement exe
          && enc.getModifiers().contains(ABSTRACT)
          && !enc.getModifiers().contains(STATIC)
          && !enc.getModifiers().contains(DEFAULT)) {
        String name = exe.getSimpleName().toString();
        methodsByName.computeIfAbsent(name, k -> new ArrayList<>()).add(exe);
      }
    }

    var fields = new ArrayList<StructField>();
    for (var entry : methodsByName.entrySet()) {
      String fieldName = entry.getKey();
      var methods = entry.getValue();

      ExecutableElement accessor = null;

      for (var m : methods) {
        var params = m.getParameters();
        if (params.isEmpty() && m.getReturnType().getKind() != TypeKind.VOID) {
          accessor = m;
        } else if (params.size() == 1
            && m.getReturnType().getKind() == TypeKind.DECLARED) {
              // TODO check if setter signature
            } else {
              processingEnv.getMessager().printError(
                  "Unsupported accessor signature for field '"
                      + fieldName + "': " + m,
                  m);
            }
      }

      if (accessor == null) {
        continue;
      }

      // Determine type from accessor return or setter parameter
      var fieldType = accessor.getReturnType();

      // Determine sequence length
      if (fieldType.getKind() == TypeKind.ARRAY) {
        // TODO error suggesting to use corresponding Buffer class
        // // Check for @Sequence on the getter/setter method
        // ExecutableElement sourceMethod = getter != null ? getter : setter;
        // sequence = getSequenceFromMethod(sourceMethod);
        // // Use component type for layout, strip annotations
        // TypeMirror componentType = ((ArrayType)
        // fieldType).getComponentType();
        // fieldType = processingEnv.getTypeUtils().erasure(componentType);
      }

      long sequence = 1;
      if (isNioBufferType(fieldType)) {
        // NIO Buffer type — extract element type
        sequence = getSequenceFromMethod(accessor);
        fieldType = extractBufferElementType(fieldType);
      }

      fields.add(new StructField(fieldName, fieldType, sequence, accessor));
    }

    return fields;
  }

  /// Get @Sequence value from a method annotation, defaulting to 1.
  private long getSequenceFromMethod(ExecutableElement method) {
    var seq = method.getAnnotation(Sequence.class);
    return seq != null ? seq.value() : 1;
  }

  private boolean isNioBufferType(TypeMirror type) {
    String erased = processingEnv.getTypeUtils().erasure(type).toString();
    return NIO_BUFFER_TYPES.contains(erased);
  }

  /// Extract the element type from a NIO Buffer type.
  private TypeMirror extractBufferElementType(TypeMirror bufferType) {
    var typeUtils = processingEnv.getTypeUtils();
    String bufName = typeUtils.erasure(bufferType).toString();
    String primitiveName = switch (bufName) {
      case "java.nio.ByteBuffer" -> "byte";
      case "java.nio.CharBuffer" -> "char";
      case "java.nio.ShortBuffer" -> "short";
      case "java.nio.IntBuffer" -> "int";
      case "java.nio.LongBuffer" -> "long";
      case "java.nio.FloatBuffer" -> "float";
      case "java.nio.DoubleBuffer" -> "double";
      default -> "byte";
    };

    // Get primitive TypeMirror via the corresponding wrapper class
    String wrapperClass = switch (primitiveName) {
      case "byte" -> "java.lang.Byte";
      case "short" -> "java.lang.Short";
      case "int" -> "java.lang.Integer";
      case "long" -> "java.lang.Long";
      case "float" -> "java.lang.Float";
      case "double" -> "java.lang.Double";
      case "char" -> "java.lang.Character";
      default -> "java.lang.Byte";
    };

    var wrapperType = processingEnv.getElementUtils()
        .getTypeElement(wrapperClass).asType();

    return typeUtils.unboxedType(wrapperType);
  }

  private void writeFile(TypeElement iface, String layoutKind)
      throws IOException {
    String packageName = processingEnv.getElementUtils()
        .getPackageOf(iface).getQualifiedName().toString();
    String className = foreignClassName(iface);
    int lastDot = className.lastIndexOf('.');
    String simpleClassName = className.substring(lastDot + 1);
    String ifaceName = iface.getSimpleName().toString();

    var fields = inferFields(iface);

    var file = processingEnv.getFiler().createSourceFile(className, iface);

    try (var out = file.openWriter()) {
      writeClassHeader(out, packageName, simpleClassName, iface);
      writeLayout(out, fields, layoutKind);
      writePathElements(out, simpleClassName, fields);
      writeVarHandles(out, simpleClassName, fields);
      writeAllocate(out, simpleClassName);
      switch (iface.getKind()) {
        case INTERFACE:
          writeConstructors(out, simpleClassName, ifaceName);
          writeFieldAccessors(out, simpleClassName, fields);
          break;
        case RECORD:
          writeRecordConverters(out, ifaceName, fields, iface);
          writeStaticAccessors(out, simpleClassName, fields);
          break;
        case ElementKind k:
          throw new IllegalArgumentException(
              "Unexpected value: " + k);
      }
      out.write("}\n");
    }
  }

  private void writeClassHeader(Writer out, String packageName,
      String className, TypeElement srcType) throws IOException {
    if (!packageName.isEmpty()) {
      out.append("package ").append(packageName).append(";\n\n");
    }

    String declaration = switch (srcType.getKind()) {
      case INTERFACE -> className + " implements "
          + srcType.getSimpleName().toString();
      case RECORD -> className;
      case ElementKind k -> throw new IllegalArgumentException(
          "Unexpected value: " + k);
    };

    out.write("""
        import java.lang.foreign.*;

        @javax.annotation.processing.Generated(
            "<generator>")
        public final class <declaration> {
        """
        .replace("<generator>", getClass().getCanonicalName())
        .replace("<declaration>", declaration));
  }

  private void writeLayout(Writer out, List<StructField> fields,
      String layoutKind) throws IOException {
    out.write("""
          public static final MemoryLayout FM$LAYOUT =
              MemoryLayout.<layout>(
                  org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
        """
        .replace("<layout>", layoutKind));

    var layoutFields = fields.stream()
        .map(f -> {
          var typeGen = new TypeGenerator(processingEnv,
              f.typeMirror(), f.sequence());

          return new MemoryLayoutGenerator.LayoutField(f.name(),
              typeGen.layout(), typeGen.typeName(), f.errorElement());
        })
        .toList();

    var layoutGen = new MemoryLayoutGenerator(processingEnv, layoutFields);
    out.write(layoutGen.layout());

    out.write("      }));\n");
  }

  private void writeAllocate(Writer out, String className) throws IOException {
    out.write("""

          public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(
              FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
          }
        """);
  }

  private void writeRecordConverters(Writer out, String srcClassName,
      List<StructField> fields, TypeElement type) throws IOException {
    var rcGens = type.getRecordComponents().stream()
        .map(rc -> new VariableGenerator(processingEnv, rc))
        .toList();

    boolean needsAllocator = fields.stream()
        .anyMatch(f -> isNestedAddress(f) && isRecord(f));

    String toMemorySegmentFields = rcGens.stream()
        .map(v -> {
          String name = v.name();
          if (needsAllocator(v))
            return "<name>(ms, ff$allocator, from.<name>());"
                .replace("<name>", name);
          return "<name>(ms, from.<name>());".replace("<name>", name);
        })
        .collect(joining("\n    ", "", ""));

    String fromMemorySegmentFields = rcGens.stream()
        .map(VariableGenerator::name)
        .map(name -> "<name>(ms)".replace("<name>", name))
        .collect(joining(",\n        ", "\n        ", ""));

    out.write("""

          public static <src> reinterpret(MemorySegment ms) {
            return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
          }

          public static void toMemorySegment(<src> from, MemorySegment ms) {
            <toMemorySegmentFields>
          }
        """
        .replace("<src>", srcClassName)
        .replace("<toMemorySegmentFields>", needsAllocator
            ? """
            try (var ff$arena = Arena.ofConfined()) {
              toMemorySegment(from, ms, ff$arena);
            }"""
            : toMemorySegmentFields));

    if (needsAllocator) {
      out.write("""

          private static void toMemorySegment(
              <src> from, MemorySegment ms, SegmentAllocator ff$allocator) {
            <toMemorySegmentFields>
          }
        """
          .replace("<src>", srcClassName)
          .replace("<toMemorySegmentFields>", toMemorySegmentFields));
    }

    out.write("""

          public static MemorySegment toMemorySegment(
              SegmentAllocator allocator, <src> from) {
            var ms = allocate(allocator);
            <toMemorySegment>
            return ms;
          }

          public static <src> fromMemorySegment(MemorySegment ms) {
            return new <src>(<fromMemorySegmentFields>);
          }
        """
        .replace("<src>", srcClassName)
        .replace("<toMemorySegment>", needsAllocator
            ? "toMemorySegment(from, ms, allocator);"
            : "toMemorySegment(from, ms);")
        .replace("<fromMemorySegmentFields>", fromMemorySegmentFields));
  }

  private void writeConstructors(Writer out,
      String className, String ifaceName) throws IOException {
    out.write("""

          public static <class> reinterpret(MemorySegment ms) {
            return new <class>(ms.reinterpret(FM$LAYOUT.byteSize()));
          }

          public final MemorySegment ms;

          public <class>(SegmentAllocator allocator) {
            this(allocate(allocator));
          }

          public <class>(MemorySegment ms) {
            this.ms = ms;
          }
        """
        .replace("<class>", className)
        .replace("<iface>", ifaceName));
  }

  private void writePathElements(Writer out, String className,
      List<StructField> fields) throws IOException {
    for (var field : fields) {
      out.write("""

            public static final MemoryLayout.PathElement FM$PE$<name> =
                MemoryLayout.PathElement.groupElement("<name>");
          """
          .replace("<name>", field.name()));
    }
  }

  private void writeVarHandles(Writer out, String className,
      List<StructField> fields) throws IOException {
    for (var field : fields) {
      if (isNestedValue(field)) {
        continue; // TODO complex/structured/indexed accessors
      }

      out.write("""

            public static final java.lang.invoke.VarHandle FM$VH$<name> =
          """
          .replace("<name>", field.name()));

      if (field.sequence() > 1) {
        out.write("""
                  FM$LAYOUT.varHandle(FM$PE$<name>);
            """
            .replace("<name>", field.name()));
      } else {
        out.write("""
                  java.lang.invoke.MethodHandles.insertCoordinates(
                      FM$LAYOUT.varHandle(FM$PE$<name>), 1, 0L);
            """
            .replace("<name>", field.name()));
      }
    }
  }

  private void writeStaticAccessors(Writer out, String className,
      List<StructField> fields) throws IOException {
    for (var field : fields) {
      if (field.sequence() > 1) {
        writeStaticAccessorsBuffer(out, field);
      } else {
        writeStaticAccessorsSimple(out, className, field);
      }
    }
  }

  private void writeStaticAccessorsSimple(Writer out, String className,
      StructField field) throws IOException {
    String name = field.name();
    String type = typeName(field);

    if (isNestedValue(field)) {
      var typeEl = (TypeElement) processingEnv.getTypeUtils()
          .asElement(field.typeMirror());
      String foreignClassName = ProcessorUtils.foreignClassName(typeEl);

      if (typeEl.getKind() == ElementKind.RECORD) {
        out.write("""

            public static <type> <name>(MemorySegment ms) {
              return <foreignClassName>.fromMemorySegment(ms.asSlice(
                  FM$LAYOUT.byteOffset(FM$PE$<name>),
                  FM$LAYOUT.select(FM$PE$<name>).byteSize()));
            }

            public static void <name>(MemorySegment ms, <type> value) {
              var layout = FM$LAYOUT.select(FM$PE$<name>);
              var slice = ms.asSlice(
                  FM$LAYOUT.byteOffset(FM$PE$<name>), layout.byteSize());
              <foreignClassName>.toMemorySegment(value, slice);
            }
          """
          .replace("<foreignClassName>", foreignClassName)
          .replace("<name>", name)
          .replace("<type>", type));
      } else {
        out.write("""

            public static <type> <name>(MemorySegment ms) {
              return new <foreignClassName>(ms.asSlice(
                  FM$LAYOUT.byteOffset(FM$PE$<name>),
                  FM$LAYOUT.select(FM$PE$<name>).byteSize()));
            }

            public static void <name>(MemorySegment ms, <type> value) {
              var layout = FM$LAYOUT.select(FM$PE$<name>);
              var slice = ms.asSlice(
                  FM$LAYOUT.byteOffset(FM$PE$<name>), layout.byteSize());
              MemorySegment.copy(((<foreignClassName>)value).ms, 0,
                  slice, 0, layout.byteSize());
            }
          """
          .replace("<foreignClassName>", foreignClassName)
          .replace("<name>", name)
          .replace("<type>", type));
      }
    } else if (isNestedAddress(field)) {
      var typeEl = (TypeElement) processingEnv.getTypeUtils()
          .asElement(field.typeMirror());
      String foreignClassName = ProcessorUtils.foreignClassName(typeEl);

      out.write("""

            public static <type> <name>(MemorySegment ms) {
              return <getter>;
            }

            public static void <name>(MemorySegment ms, <type> value) {
              FM$VH$<name>.set(ms, <address>);
            }
          """
          .replace("<getter>", nestedAddressGetter(field, "ms"))
          .replace("<address>", nestedAddressValue(field, "value"))
          .replace("<foreignClassName>", foreignClassName)
          .replace("<name>", name)
          .replace("<type>", type));

      if (isRecord(field)) {
        out.write("""

            public static void <name>(
                MemorySegment ms, SegmentAllocator allocator, <type> value) {
              FM$VH$<name>.set(ms,
                  <foreignClassName>.toMemorySegment(allocator, value));
            }
          """
            .replace("<foreignClassName>", foreignClassName)
            .replace("<name>", name)
            .replace("<type>", type));
      }
    } else {
      out.write("""

            public static <type> <name>(MemorySegment ms) {
              return (<type>) FM$VH$<name>.get(ms);
            }

            public static void <name>(MemorySegment ms, <type> value) {
              FM$VH$<name>.set(ms, value);
            }
          """
          .replace("<name>", name)
          .replace("<type>", type));
    }
  }

  private void writeStaticAccessorsBuffer(Writer out, StructField field)
      throws IOException {
    // TODO accessors for array/buffer types in records
  }

  private void writeFieldAccessors(Writer out, String className,
      List<StructField> fields) throws IOException {
    for (var field : fields) {
      if (field.sequence() > 1) {
        writeAccessorsBuffer(out, field);
      } else {
        writeAccessorsSimple(out, className, field);
      }
    }
  }

  private void writeAccessorsSimple(Writer out, String className,
      StructField field) throws IOException {
    String name = field.name();
    String type = typeName(field);

    if (isNestedValue(field)) {
      var typeEl = (TypeElement) processingEnv.getTypeUtils()
          .asElement(field.typeMirror());

      String foreignClassName = ProcessorUtils.foreignClassName(typeEl);

      if (typeEl.getKind() == ElementKind.RECORD) {
        out.write("""

            public <type> <name>() {
              return <foreignClassName>.fromMemorySegment(ms.asSlice(
                  FM$LAYOUT.byteOffset(FM$PE$<name>),
                  FM$LAYOUT.select(FM$PE$<name>).byteSize()));
            }

            public <class> <name>(<type> value) {
              var layout = FM$LAYOUT.select(FM$PE$<name>);
              var slice = ms.asSlice(
                  FM$LAYOUT.byteOffset(FM$PE$<name>), layout.byteSize());
              <foreignClassName>.toMemorySegment(value, slice);
              return this;
            }
          """
          .replace("<class>", className)
          .replace("<foreignClassName>", foreignClassName)
          .replace("<name>", name)
          .replace("<type>", type));
      } else {
        out.write("""

            public <type> <name>() {
              return new <foreignClassName>(ms.asSlice(
                  FM$LAYOUT.byteOffset(FM$PE$<name>),
                  FM$LAYOUT.select(FM$PE$<name>).byteSize()));
            }

            public <class> <name>(<type> value) {
              var layout = FM$LAYOUT.select(FM$PE$<name>);
              var slice = ms.asSlice(
                  FM$LAYOUT.byteOffset(FM$PE$<name>), layout.byteSize());
              MemorySegment.copy(((<foreignClassName>)value).ms, 0,
                  slice, 0, layout.byteSize());
              return this;
            }
          """
          .replace("<class>", className)
          .replace("<foreignClassName>", foreignClassName)
          .replace("<name>", name)
          .replace("<type>", type));
      }
    } else if (isNestedAddress(field)) {
      out.write("""

            public <type> <name>() {
              return <getter>;
            }

            public <class> <name>(<type> value) {
              FM$VH$<name>.set(ms, <address>);
              return this;
            }
          """
          .replace("<class>", className)
          .replace("<name>", name)
          .replace("<type>", type)
          .replace("<getter>", nestedAddressGetter(field, "ms"))
          .replace("<address>", nestedAddressValue(field, "value")));
    } else {
      out.write("""

            public <type> <name>() {
              return (<type>) FM$VH$<name>.get(ms);
            }

            public <class> <name>(<type> value) {
              FM$VH$<name>.set(ms, value);
              return this;
            }
          """
          .replace("<class>", className)
          .replace("<name>", name)
          .replace("<type>", type));
    }
  }

  private void writeAccessorsBuffer(Writer out, StructField field)
      throws IOException {
    String name = field.name();
    String type = field.typeMirror().toString();
    String capType = capitalize(type);
    long size = field.sequence();

    out.write("""

          public static final long FM$OFFSET$<name> =
              FM$LAYOUT.byteOffset(FM$PE$<name>);

          public static final long FM$SIZE$<name> =
              FM$LAYOUT.select(FM$PE$<name>).byteSize();

          private MemorySegment FM$MS$<name>;

          public MemorySegment <name>$MemorySegment() {
            if (FM$MS$<name> == null) {
              FM$MS$<name> = ms.asSlice(FM$OFFSET$<name>, FM$SIZE$<name>);
            }
            return FM$MS$<name>;
          }
        """
        .replace("<name>", name));

    out.write("""

          private java.nio.<Type>Buffer FM$BB$<name>;

          public java.nio.<Type>Buffer <name>() {
            if (FM$BB$<name> == null) {
              FM$BB$<name> = <name>$MemorySegment().asByteBuffer()<buffer>;
            }
            return FM$BB$<name>;
          }
        """
        .replace("<name>", name)
        .replace("<Type>", capType)
        .replace("<buffer>", type.equals("byte")
            ? "" : ".as" + capType + "Buffer()"));

    out.write("""

          /** get element at index */
          public <type> <name>(int index) {
            return <name>$MemorySegment()
              .getAtIndex(ValueLayout.JAVA_<TYPE>, index);
          }

          /** set element at index */
          public void <name>(int index, <type> value) {
            <name>$MemorySegment()
              .setAtIndex(ValueLayout.JAVA_<TYPE>, index, value);
          }

          /** replace values from array */
          public void <name>(<type>[] value) {
            if (value.length != <size>) {
              throw new IllegalArgumentException();
            }
            MemorySegment.copy(value, 0,
                <name>$MemorySegment(), ValueLayout.JAVA_<TYPE>, 0, <size>);
          }
        """
        .replace("<name>", name)
        .replace("<type>", type)
        .replace("<TYPE>", type.toUpperCase(Locale.ROOT))
        .replace("<size>", Long.toString(size)));
  }

  private boolean isNested(StructField field) {
    var typeEl = (TypeElement) processingEnv.getTypeUtils()
        .asElement(field.typeMirror());

    return typeEl != null
        && (typeEl.getAnnotation(ForeignStruct.class) != null
            || typeEl.getAnnotation(ForeignUnion.class) != null);
  }

  private boolean isNestedValue(StructField field) {
    return isNested(field)
        && new TypeGenerator(processingEnv, field.typeMirror(), field.sequence())
            .isValue();
  }

  private boolean isNestedAddress(StructField field) {
    return isNested(field)
        && new TypeGenerator(processingEnv, field.typeMirror(), field.sequence())
            .isAddress();
  }

  private boolean isRecord(StructField field) {
    var typeEl = (TypeElement) processingEnv.getTypeUtils()
        .asElement(field.typeMirror());
    return typeEl != null && typeEl.getKind() == ElementKind.RECORD;
  }

  private boolean needsAllocator(VariableGenerator variable) {
    return variable.isRecord() && variable.isAddress();
  }

  private String typeName(StructField field) {
    return new TypeGenerator(processingEnv, field.typeMirror(), field.sequence())
        .typeName();
  }

  private String nestedAddressGetter(StructField field, String segment) {
    var typeEl = (TypeElement) processingEnv.getTypeUtils()
        .asElement(field.typeMirror());
    String foreignClassName = ProcessorUtils.foreignClassName(typeEl);
    String name = field.name();
    String address = "(MemorySegment) FM$VH$" + name + ".get(" + segment
        + ")";

    if (typeEl.getKind() == ElementKind.RECORD)
      return foreignClassName + ".reinterpret(" + address + ")";

    return foreignClassName + ".reinterpret(" + address + ")";
  }

  private String nestedAddressValue(StructField field, String value) {
    if (isRecord(field)) {
      var typeEl = (TypeElement) processingEnv.getTypeUtils()
          .asElement(field.typeMirror());
      return ProcessorUtils.foreignClassName(typeEl)
          + ".toMemorySegment(Arena.ofAuto(), " + value + ")";
    }

    var typeEl = (TypeElement) processingEnv.getTypeUtils()
        .asElement(field.typeMirror());
    return "((" + ProcessorUtils.foreignClassName(typeEl) + ")" + value + ").ms";
  }

  private String capitalize(String name) {
    return Character.toTitleCase(name.charAt(0)) + name.substring(1);
  }
}

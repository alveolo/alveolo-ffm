package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.RELEASE_25;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemorySimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.qualifyName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Union;
import org.alveolo.ffm.Virtual;

@SupportedAnnotationTypes({
  "org.alveolo.ffm.Struct",
  "org.alveolo.ffm.Union",
  "org.alveolo.ffm.Virtual",
})
@SupportedSourceVersion(RELEASE_25)
public class ForeignMemoryProcessor extends AbstractProcessor {
  private static final String VTABLE_FIELD = "ff$vtbl";

  record InferredFields(
      List<VariableGenerator> fields,
      List<ExecutableElement> unsupportedMethods
  ) {}

  record ObjectMethods(
      List<ExecutableElement> methods,
      List<ExecutableElement> virtualMethods,
      List<ExecutableElement> symbolMethods
  ) {
    static ObjectMethods empty() {
      return new ObjectMethods(List.of(), List.of(), List.of());
    }

    boolean hasVirtualMethods() {
      return !virtualMethods.isEmpty();
    }

    boolean hasSymbolMethods() {
      return !symbolMethods.isEmpty();
    }
  }

  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var messager = processingEnv.getMessager();

    if (roundEnv.processingOver()) return true;

    for (var annotation : annotations) {
      if (annotation.getQualifiedName().contentEquals(
          Virtual.class.getCanonicalName())) {
        validateVirtualAnnotations(roundEnv.getElementsAnnotatedWith(
            annotation));
        continue;
      }

      for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element instanceof TypeElement type) {
          switch (type.getKind()) {
            case INTERFACE:
            case RECORD:
              try {
                var struct = type.getAnnotation(Struct.class);
                if (struct != null) {
                  validateSimpleClassName(annotation, struct, struct.name());
                  validateTopLevelType(type, struct);
                  if (struct.vtable()
                      && type.getKind() == ElementKind.RECORD) {
                    messager.printError(
                        "@Struct(vtable = true) can only be applied to an "
                            + "interface, not RECORD",
                        type);
                  } else {
                    writeFile(type, "struct", struct.vtable());
                  }
                }

                var union = type.getAnnotation(Union.class);
                if (union != null) {
                  validateSimpleClassName(annotation, union, union.name());
                  validateTopLevelType(type, union);
                  if (type.getKind() == ElementKind.RECORD) {
                    messager.printError("@" + annotation.getSimpleName()
                        + " can only be applied to an interface, not "
                        + ElementKind.RECORD, type);
                  } else {
                    writeFile(type, "union", false);
                  }
                }
              } catch (ProcessorError e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                    e.getMessage(), e.getElement());
              } catch (Throwable e) {
                var sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                messager.printError(sw.toString(), type);
              }
              break;
            case ElementKind kind:
              messager.printError("@" + annotation.getSimpleName()
                  + " can only be applied to an interface, not " + kind, type);
          }
        }
      }
    }

    return true;
  }

  private void validateVirtualAnnotations(Set<? extends Element> elements) {
    for (var element : elements) {
      if (!(element instanceof ExecutableElement method)
          || !(method.getEnclosingElement() instanceof TypeElement owner)) {
        processingEnv.getMessager().printError(
            "@Virtual is only allowed on methods of @Struct(vtable = true)",
            element);
        continue;
      }

      var struct = owner.getAnnotation(Struct.class);
      if (struct == null || !struct.vtable()) {
        processingEnv.getMessager().printError(
            "@Virtual is only allowed on @Struct(vtable = true) methods",
            method);
        continue;
      }

      if (method.getKind() != ElementKind.METHOD
          || !method.getModifiers().contains(ABSTRACT)
          || method.getModifiers().contains(STATIC)
          || method.getModifiers().contains(DEFAULT)) {
        processingEnv.getMessager().printError(
            "@Virtual is only allowed on abstract instance methods",
            method);
      }
    }
  }

  /// Infer struct fields from interface accessor methods.
  private InferredFields inferFields(
      TypeElement iface, boolean excludeObjectMethods) {
    if (iface.getKind() == ElementKind.RECORD)
      return new InferredFields(iface.getRecordComponents().stream()
          .map(component -> new VariableGenerator(processingEnv, component))
          .toList(), List.of());

    // Group methods by name
    Map<String, List<ExecutableElement>> methodsByName = new LinkedHashMap<>();
    for (var enc : iface.getEnclosedElements()) {
      if (enc instanceof ExecutableElement exe
          && enc.getModifiers().contains(ABSTRACT)
          && !enc.getModifiers().contains(STATIC)
          && !enc.getModifiers().contains(DEFAULT)) {
        if (excludeObjectMethods && isObjectMethod(exe)) {
          continue;
        }
        String name = exe.getSimpleName().toString();
        methodsByName.computeIfAbsent(name, _ -> new ArrayList<>()).add(exe);
      }
    }

    var fields = new ArrayList<VariableGenerator>();
    var unsupportedMethods = new ArrayList<ExecutableElement>();
    for (var entry : methodsByName.entrySet()) {
      String fieldName = entry.getKey();
      var methods = entry.getValue();

      ExecutableElement accessor = null;
      var fieldMethods = new ArrayList<ExecutableElement>();

      for (var m : methods) {
        var params = m.getParameters();
        if (params.isEmpty() && m.getReturnType().getKind() != TypeKind.VOID) {
          accessor = m;
        } else {
          fieldMethods.add(m);
        }
      }

      if (accessor == null) {
        processingEnv.getMessager().printError(
            "Field '" + fieldName + "' has no accessor",
            methods.get(0));
        for (var m : methods) {
          addUnsupportedMethod(unsupportedMethods, m);
        }
        continue;
      }

      // Determine type from accessor return.
      var fieldType = accessor.getReturnType();
      if (fieldType.getKind() == TypeKind.ARRAY) {
        processingEnv.getMessager().printError(
            "Array fields are not supported, use "
                + bufferSuggestion(((ArrayType) fieldType).getComponentType())
                + " instead",
            accessor);
        for (var m : methods) {
          addUnsupportedMethod(unsupportedMethods, m);
        }
        continue;
      }

      for (var method : fieldMethods) {
        processingEnv.getMessager().printError(
            "Unsupported accessor signature for field '"
                + fieldName + "': " + method,
            method);
      }

      fields.add(new VariableGenerator(processingEnv, fieldName, fieldType,
          TypeGenerator.sequence(fieldType, accessor), accessor));
    }

    return new InferredFields(fields, unsupportedMethods);
  }

  private void addUnsupportedMethod(
      List<ExecutableElement> unsupportedMethods, ExecutableElement method) {
    if (!unsupportedMethods.contains(method)) {
      unsupportedMethods.add(method);
    }
  }

  private boolean isObjectMethod(ExecutableElement method) {
    return method.getAnnotation(Virtual.class) != null
        || method.getAnnotation(Symbol.class) != null;
  }

  private ObjectMethods objectMethods(TypeElement iface) {
    var methods = new ArrayList<ExecutableElement>();
    var virtualMethods = new ArrayList<ExecutableElement>();
    var symbolMethods = new ArrayList<ExecutableElement>();

    for (var enc : iface.getEnclosedElements()) {
      if (enc instanceof ExecutableElement method
          && method.getModifiers().contains(ABSTRACT)
          && !method.getModifiers().contains(STATIC)
          && !method.getModifiers().contains(DEFAULT)) {
        if (isObjectMethod(method)) {
          methods.add(method);
        }
        if (method.getAnnotation(Virtual.class) != null) {
          virtualMethods.add(method);
        }
        if (method.getAnnotation(Symbol.class) != null) {
          symbolMethods.add(method);
        }
      }
    }

    return new ObjectMethods(methods, virtualMethods, symbolMethods);
  }

  private boolean validateObjectMethods(
      TypeElement iface, ObjectMethods objectMethods, boolean vtable) {
    var valid = true;

    if (vtable) {
      for (var member : iface.getEnclosedElements()) {
        if (member.getSimpleName().contentEquals(VTABLE_FIELD)) {
          processingEnv.getMessager().printError(
              "'" + VTABLE_FIELD + "' is reserved for generated vtable access",
              member);
          valid = false;
        }
      }
    }

    if (!vtable && objectMethods.hasVirtualMethods()) {
      for (var method : objectMethods.virtualMethods()) {
        processingEnv.getMessager().printError(
            "@Virtual is only allowed on @Struct(vtable = true) methods",
            method);
      }
      valid = false;
    }

    var slots = new LinkedHashMap<Integer, ExecutableElement>();
    for (var method : objectMethods.virtualMethods()) {
      if (method.getAnnotation(Symbol.class) != null) {
        processingEnv.getMessager().printError(
            "@Virtual and @Symbol cannot be used on the same method", method);
        valid = false;
      }

      var slot = method.getAnnotation(Virtual.class).value();
      if (slot < 0) {
        processingEnv.getMessager().printError(
            "@Virtual value must be non-negative", method);
        valid = false;
        continue;
      }

      var previous = slots.putIfAbsent(slot, method);
      if (previous != null) {
        processingEnv.getMessager().printError(
            "Duplicate @Virtual slot: " + slot, method);
        processingEnv.getMessager().printError(
            "Duplicate @Virtual slot: " + slot, previous);
        valid = false;
      }
    }

    return valid;
  }

  private TypeElement symbolOwner(
      TypeElement iface, ObjectMethods objectMethods) {
    if (!objectMethods.hasSymbolMethods()) return null;

    var symbols = symbols(iface);
    if (symbols == null || symbols.toString().equals(Void.class.getName())) {
      processingEnv.getMessager().printError(
          "@Struct symbols is required when @Symbol methods are used", iface);
      return null;
    }

    var owner = processingEnv.getTypeUtils().asElement(symbols);
    if (!(owner instanceof TypeElement type)
        || type.getKind() != ElementKind.INTERFACE
        || type.getAnnotation(ForeignInterface.class) == null) {
      processingEnv.getMessager().printError(
          "@Struct symbols must reference an @ForeignInterface", iface);
      return null;
    }

    return type;
  }

  private TypeMirror symbols(TypeElement iface) {
    for (var mirror : iface.getAnnotationMirrors()) {
      if (!mirror.getAnnotationType().toString()
          .equals(Struct.class.getCanonicalName())) {
        continue;
      }

      for (var entry : mirror.getElementValues().entrySet()) {
        if (entry.getKey().getSimpleName().contentEquals("symbols"))
          return (TypeMirror) entry.getValue().getValue();
      }
    }

    return null;
  }

  /// Validate record components. Array and buffer components are not supported
  /// in record converters yet.
  private boolean validateRecordComponents(TypeElement type) {
    if (type.getKind() != ElementKind.RECORD) return true;

    var valid = true;
    for (var component : type.getRecordComponents()) {
      var componentType = component.asType();
      if (componentType.getKind() == TypeKind.ARRAY) {
        processingEnv.getMessager().printError(
            "Array fields are not supported, use "
                + bufferSuggestion(
                    ((ArrayType) componentType).getComponentType())
                + " instead",
            component);
        valid = false;
      } else if (new TypeGenerator(processingEnv, componentType)
          .isNioBuffer()) {
            processingEnv.getMessager().printError(
                "Buffer fields are not supported on records, use an interface "
                    + "@Struct instead",
                component);
            valid = false;
          }
    }

    return valid;
  }

  private String bufferSuggestion(TypeMirror componentType) {
    return switch (componentType.getKind()) {
      case BYTE -> "java.nio.ByteBuffer";
      case CHAR -> "java.nio.CharBuffer";
      case SHORT -> "java.nio.ShortBuffer";
      case INT -> "java.nio.IntBuffer";
      case LONG -> "java.nio.LongBuffer";
      case FLOAT -> "java.nio.FloatBuffer";
      case DOUBLE -> "java.nio.DoubleBuffer";
      default -> "a java.nio Buffer type";
    };
  }

  private void writeFile(TypeElement iface, String kind, boolean vtable)
      throws IOException {
    validateRecordComponents(iface);

    var elements = processingEnv.getElementUtils();
    String packageName = packageName(iface, elements);
    String ifaceSimpleName = iface.getSimpleName().toString();
    String className = foreignMemoryClassName(iface);
    String simpleClassName = foreignMemorySimpleClassName(iface);
    String vtableSimpleName = ifaceSimpleName + "Vtbl";

    var isStructInterface = kind.equals("struct")
        && iface.getKind() == ElementKind.INTERFACE;
    var objectMethods = isStructInterface
        ? objectMethods(iface)
        : ObjectMethods.empty();
    if (!validateObjectMethods(iface, objectMethods, vtable)) return;

    var symbolOwner = symbolOwner(iface, objectMethods);
    if (objectMethods.hasSymbolMethods() && symbolOwner == null) return;
    var symbolOwnerClassName = symbolOwner == null
        ? null
        : foreignInterfaceClassName(symbolOwner);
    var symbolGenerators = symbolGenerators(objectMethods,
        symbolOwnerClassName);
    var virtualGenerators = virtualGenerators(objectMethods.virtualMethods());

    if (objectMethods.hasVirtualMethods()) {
      writeDispatchTable(iface, packageName, ifaceSimpleName,
          vtableSimpleName, virtualGenerators);
    }

    var structFields = inferFields(iface, isStructInterface);
    var fields = structFields.fields();
    validateFields(fields);

    var file = processingEnv.getFiler().createSourceFile(className, iface);

    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.append("package ").append(packageName).append(";\n\n");
      }

      String declaration = switch (iface.getKind()) {
        case INTERFACE -> simpleClassName + " implements "
            + ifaceSimpleName;
        case RECORD -> simpleClassName;
        case ElementKind k1 -> throw new IllegalArgumentException(
            "Unexpected value: " + k1);
      };

      out.write("""
          import java.lang.foreign.*;
          <methodHandleImport>
          @javax.annotation.processing.Generated(
              "<generator>")
          public final class <declaration> {
          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<declaration>", declaration)
          .replace("<methodHandleImport>", objectMethods.hasSymbolMethods()
              ? "import java.lang.invoke.MethodHandle;\n" : ""));

      writeLayout(out, fields, kind, vtable);
      writePathElements(out, simpleClassName, fields, vtable);
      writeVarHandles(out, simpleClassName, fields, vtable);
      writeAllocate(out, simpleClassName);
      switch (iface.getKind()) {
        case INTERFACE:
          writeConstructors(out, simpleClassName, vtableSimpleName,
              objectMethods.hasVirtualMethods());
          writeFieldAccessors(out, simpleClassName, fields);
          writeUnsupportedMethods(out, structFields.unsupportedMethods());
          writeSymbolHolder(out, symbolGenerators);
          writeObjectMethods(out, objectMethods, symbolGenerators,
              virtualGenerators);
          break;
        case RECORD:
          writeRecordConverters(out, ifaceSimpleName, iface);
          writeStaticAccessors(out, fields);
          break;
        case ElementKind k:
          throw new IllegalArgumentException("Unexpected value: " + k);
      }

      out.write("}\n");
    }
  }

  private void writeDispatchTable(TypeElement iface, String packageName,
      String ifaceSimpleName, String vtableSimpleName,
      List<ExecutableGenerator> generators) throws IOException {
    String vtableClassName = qualifyName(packageName, vtableSimpleName);

    var file = processingEnv.getFiler()
        .createSourceFile(vtableClassName, iface);

    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
          @javax.annotation.processing.Generated(
              "<generator>")
          @org.alveolo.ffm.DispatchTable
          interface <name>Vtbl {
          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<name>", ifaceSimpleName));

      for (var generator : generators) {
        if (generator.hasErrors) {
          continue;
        }

        var method = generator.element;
        out.write("""

              @org.alveolo.ffm.Slot(<slot>)
              <signature>;
            """
            .replace("<slot>", Integer.toString(virtualSlot(method)))
            .replace("<signature>",
                dispatchTableMethodSignature(iface, method)));
      }

      out.write("}\n");
    }
  }

  private String dispatchTableMethodSignature(TypeElement iface,
      ExecutableElement method) {
    var params = new ArrayList<String>();
    params.add(iface.getSimpleName() + " ff$self");
    params.addAll(method.getParameters().stream()
        .map(this::sourceParameter)
        .toList());

    return sourceReturnType(method)
        + " " + method.getSimpleName()
        + params.stream().collect(joining(",\n      ", "(\n      ", ")"));
  }

  private List<ExecutableGenerator> symbolGenerators(
      ObjectMethods objectMethods, String symbolOwnerClassName) {
    var generators = new ArrayList<ExecutableGenerator>();
    var index = 0;
    for (var method : objectMethods.symbolMethods()) {
      generators.add(symbolGenerator(method, index++, symbolOwnerClassName));
    }
    return generators;
  }

  private List<ExecutableGenerator> virtualGenerators(
      List<ExecutableElement> methods) {
    return methods.stream()
        .map(method -> new ExecutableGenerator(
            processingEnv, method, "FF$VH$unused", true))
        .toList();
  }

  private void writeSymbolHolder(
      Writer out, List<ExecutableGenerator> symbolGenerators)
      throws IOException {
    if (symbolGenerators.isEmpty()) return;

    out.write("""

          private static final class FF$SYMBOLS {
        """);

    for (var generator : symbolGenerators) {
      if (!generator.hasErrors) {
        out.write(generator.methodHandleDeclaration()
            .replace("\n  ", "\n    "));
      }
    }

    out.write("""
          }
        """);
  }

  private void writeObjectMethods(Writer out, ObjectMethods objectMethods,
      List<ExecutableGenerator> symbolGenerators,
      List<ExecutableGenerator> virtualGenerators)
      throws IOException {
    var symbolIndex = 0;
    var virtualIndex = 0;
    for (var method : objectMethods.methods()) {
      if (method.getAnnotation(Virtual.class) != null) {
        writeVirtualMethod(out, virtualGenerators.get(virtualIndex++));
      } else {
        var generator = symbolGenerators.get(symbolIndex++);
        out.write(generator.methodOnly(
            "FF$SYMBOLS." + generator.methodHandleName));
      }
    }
  }

  private ExecutableGenerator symbolGenerator(ExecutableElement method,
      int index, String symbolOwnerClassName) {
    return new ExecutableGenerator(
        processingEnv, method, "FF$MH$" + index, false,
        List.of(new ExecutableGenerator.NativeArgument(
            "ValueLayout.ADDRESS", "this.ms")),
        symbolOwnerClassName + ".FF$LINKER",
        symbolOwnerClassName + ".FF$LOOKUP");
  }

  private void writeVirtualMethod(Writer out, ExecutableGenerator generator)
      throws IOException {
    if (generator.hasErrors) {
      out.write(generator.methodOnly(""));
      return;
    }

    var method = generator.element;
    var args = new ArrayList<String>();
    args.add("this");
    args.addAll(method.getParameters().stream()
        .map(VariableElement::getSimpleName)
        .map(Object::toString)
        .toList());
    String call = "ff$vtbl()." + method.getSimpleName()
        + args.stream().collect(joining(", ", "(", ")"));

    String statement = method.getReturnType().getKind() == TypeKind.VOID
        ? call + ";"
        : "return " + call + ";";

    out.write("""

          <signature> {
            <statement>
          }
        """
        .replace("<signature>", generator.signature())
        .replace("<statement>", statement));
  }

  private String sourceReturnType(ExecutableElement method) {
    return method.getReturnType().toString();
  }

  private String sourceMethodSignature(ExecutableElement method) {
    return "public " + sourceReturnType(method) + " "
        + method.getSimpleName()
        + method.getParameters().stream()
            .map(this::sourceParameter)
            .collect(joining(",\n      ", "(\n      ", ")"));
  }

  private String sourceParameter(VariableElement parameter) {
    return parameter.asType() + " " + parameter.getSimpleName();
  }

  private String foreignInterfaceClassName(TypeElement type) {
    return ProcessorUtils.foreignInterfaceClassName(
        type, processingEnv.getElementUtils());
  }

  private String foreignMemoryClassName(TypeElement type) {
    return ProcessorUtils.foreignMemoryClassName(
        type, processingEnv.getElementUtils());
  }

  private int virtualSlot(ExecutableElement method) {
    return method.getAnnotation(Virtual.class).value();
  }

  private void writeLayout(Writer out, List<VariableGenerator> fields,
      String kind, boolean vtable) throws IOException {
    out.write("""
          public static final MemoryLayout FM$LAYOUT =
              MemoryLayout.<kind>Layout(
                  org.alveolo.ffm.ForeignUtils.<kind>Pad(new MemoryLayout [] {
        """
        .replace("<kind>", kind));

    if (vtable) {
      out.write("        ValueLayout.ADDRESS.withName(\""
          + VTABLE_FIELD + "\"),\n");
    }

    var layoutFields = fields.stream()
        .map(f -> new MemoryLayoutGenerator.LayoutField(f.name(),
            f.layout(), f.unsupported(), f.typeName(), f.element))
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

  private void writeRecordConverters(
      Writer out, String srcClassName, TypeElement type) throws IOException {
    var rcGens = type.getRecordComponents().stream()
        .map(rc -> new VariableGenerator(processingEnv, rc))
        .toList();

    boolean needsAllocator = rcGens.stream()
        .anyMatch(this::needsAllocatorWrite);

    String fromMemorySegmentFields = rcGens.stream()
        .map(gen -> gen.name() + "(ms)")
        .collect(joining(",\n        ", "\n        ", ""));

    var toMemorySegmentMethod = needsAllocator
        ? """
            public static void toMemorySegment(
                <src> from, MemorySegment ms, SegmentAllocator ff$allocator) {
              <toMemorySegmentFields>
            }
            """
        : """
            public static void toMemorySegment(<src> from, MemorySegment ms) {
              <toMemorySegmentFields>
            }
            """;

    out.write("""

          public static <src> reinterpret(MemorySegment ms) {
            return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
          }

        <toMemorySegmentMethod>

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
        .replace("<toMemorySegmentMethod>",
            "  " + toMemorySegmentMethod.stripTrailing()
                .replace("\n", "\n  "))
        .replace("<toMemorySegmentFields>",
            recordFieldWrites(rcGens, needsAllocator))
        .replace("<src>", srcClassName)
        .replace("<toMemorySegment>", needsAllocator
            ? "toMemorySegment(from, ms, allocator);"
            : "toMemorySegment(from, ms);")
        .replace("<fromMemorySegmentFields>", fromMemorySegmentFields));
  }

  private String recordFieldWrites(
      List<VariableGenerator> variables, boolean withAllocator) {
    return variables.stream()
        .map(v -> recordFieldWrite(v, withAllocator))
        .collect(joining("\n    ", "", ""));
  }

  private String recordFieldWrite(
      VariableGenerator variable, boolean withAllocator) {
    var name = variable.name();
    return withAllocator && needsAllocatorWrite(variable)
        ? name + "(ms, ff$allocator, from." + name + "());"
        : name + "(ms, from." + name + "());";
  }

  private void writeConstructors(Writer out,
      String className, String vtableTypeName, boolean hasVirtualMethods)
      throws IOException {
    out.write("""

          public static <class> reinterpret(MemorySegment ms) {
            return new <class>(ms.reinterpret(FM$LAYOUT.byteSize()));
          }

          public final MemorySegment ms;
        """
        .replace("<class>", className));

    if (hasVirtualMethods) {
      out.write("""

            private final <iface>Vtbl ff$vtbl;
          """
          .replace("<iface>Vtbl", vtableTypeName));
    }

    out.write("""

          public <class>(SegmentAllocator allocator) {
            this(allocate(allocator));
          }

          public <class>(MemorySegment ms) {
            this.ms = ms;
        """
        .replace("<class>", className));

    if (hasVirtualMethods) {
      out.write("    this.ff$vtbl = " + vtableTypeName
          + "FD.reinterpret((MemorySegment) FM$VH$ff$vtbl.get(ms));\n");
    }

    out.write("""
          }
        """);

    if (hasVirtualMethods) {
      out.write("""

            private <iface>Vtbl ff$vtbl() {
              return ff$vtbl;
            }
          """
          .replace("<iface>Vtbl", vtableTypeName));
    }
  }

  private void writePathElements(Writer out, String className,
      List<VariableGenerator> fields, boolean vtable) throws IOException {
    if (vtable) {
      out.write("""

            public static final MemoryLayout.PathElement FM$PE$ff$vtbl =
                MemoryLayout.PathElement.groupElement("ff$vtbl");
          """);
    }

    for (var field : fields) {
      out.write("""

            public static final MemoryLayout.PathElement FM$PE$<name> =
                MemoryLayout.PathElement.groupElement("<name>");
          """
          .replace("<name>", field.name()));
    }
  }

  private void writeVarHandles(Writer out, String className,
      List<VariableGenerator> fields, boolean vtable) throws IOException {
    if (vtable) {
      out.write("""

            public static final java.lang.invoke.VarHandle FM$VH$ff$vtbl =
                java.lang.invoke.MethodHandles.insertCoordinates(
                    FM$LAYOUT.varHandle(FM$PE$ff$vtbl), 1, 0L);
          """);
    }

    for (var field : fields) {
      if (field.isNioBuffer()
          || (field.isForeignMemory() && field.isValue())) {
        continue; // TODO complex/structured/indexed accessors
      }

      var initializer = field.sequence > 1
          ? "FM$LAYOUT.varHandle(FM$PE$<name>);"
          : """
              java.lang.invoke.MethodHandles.insertCoordinates(
                  FM$LAYOUT.varHandle(FM$PE$<name>), 1, 0L);
              """.stripTrailing();

      out.write("""

            public static final java.lang.invoke.VarHandle FM$VH$<name> =
                <initializer>
          """
          .replace("<initializer>",
              initializer.replace("\n", "\n      "))
          .replace("<name>", field.name()));
    }
  }

  /// Receiver style for generated field accessors: records use static accessors
  /// over an explicit MemorySegment parameter, memory-backed interfaces use
  /// instance accessors over `this.ms` with fluent setters.
  private record AccessorTarget(boolean isStatic, String className) {
    static final AccessorTarget STATIC = new AccessorTarget(true, null);

    static AccessorTarget fluent(String className) {
      return new AccessorTarget(false, className);
    }

    String getterHead(VariableGenerator field) {
      return (isStatic
          ? "public static <type> <name>(MemorySegment ms)"
          : "public <type> <name>()")
              .replace("<type>", field.typeName())
              .replace("<name>", field.name());
    }

    String setterHead(VariableGenerator field, boolean needsAllocator) {
      var head = isStatic
          ? staticSetterHead(needsAllocator)
          : fluentSetterHead(needsAllocator).replace("<class>", className);

      return head
          .replace("<type>", field.typeName())
          .replace("<name>", field.name());
    }

    private String staticSetterHead(boolean needsAllocator) {
      return needsAllocator
          ? "public static void <name>(\n"
              + "      MemorySegment ms, SegmentAllocator allocator,"
              + " <type> value)"
          : "public static void <name>(MemorySegment ms, <type> value)";
    }

    private String fluentSetterHead(boolean needsAllocator) {
      return needsAllocator
          ? "public <class> <name>(\n"
              + "      SegmentAllocator allocator, <type> value)"
          : "public <class> <name>(<type> value)";
    }
  }

  private void writeStaticAccessors(Writer out, List<VariableGenerator> fields)
      throws IOException {
    for (var field : fields) {
      writeAccessorsSimple(out, AccessorTarget.STATIC, field);
    }
  }

  private void writeFieldAccessors(Writer out, String className,
      List<VariableGenerator> fields) throws IOException {
    var target = AccessorTarget.fluent(className);
    for (var field : fields) {
      if (field.isNioBuffer()) {
        writeAccessorsBuffer(out, field);
      } else {
        writeAccessorsSimple(out, target, field);
      }
    }
  }

  private void writeUnsupportedMethods(Writer out,
      List<ExecutableElement> unsupportedMethods) throws IOException {
    for (var method : unsupportedMethods) {
      out.write("""

            <signature> {
              throw new RuntimeException("Check compile errors!");
            }
          """
          .replace("<signature>", sourceMethodSignature(method)));
    }
  }

  private void writeThrowingStaticAccessors(Writer out, VariableGenerator field)
      throws IOException {
    String name = field.name();
    String type = field.typeName();

    out.write("""

          public static <type> <name>(MemorySegment ms) {
            throw new RuntimeException("Check compile errors!");
          }

          public static void <name>(MemorySegment ms, <type> value) {
            throw new RuntimeException("Check compile errors!");
          }

          public static void <name>(
              MemorySegment ms, SegmentAllocator allocator, <type> value) {
            throw new RuntimeException("Check compile errors!");
          }
        """
        .replace("<name>", name)
        .replace("<type>", type));
  }

  private void writeThrowingFieldAccessors(Writer out, String className,
      VariableGenerator field, boolean writeSetter) throws IOException {
    String name = field.name();
    String type = field.typeName();
    var setter = writeSetter
        ? """

            public <class> <name>(<type> value) {
              throw new RuntimeException("Check compile errors!");
            }
            """
        : "";

    out.write("""

          public <type> <name>() {
            throw new RuntimeException("Check compile errors!");
          }
          <setter>
        """
        .replace("<setter>", setter)
        .replace("<class>", className)
        .replace("<name>", name)
        .replace("<type>", type));
  }

  /// Writes getter and setter accessors for a simple (non-buffer) field in the
  /// accessor style selected by the target.
  private void writeAccessorsSimple(Writer out, AccessorTarget target,
      VariableGenerator field) throws IOException {
    String name = field.name();

    if (!target.isStatic() && field.isPrimitiveAddress()) {
      reportMemoryBackedPrimitiveAddressField(field);
      writeThrowingFieldAccessors(out, target.className(), field, false);
      return;
    }

    if (fieldAccessorsShouldThrow(target, field)) {
      writeThrowingAccessors(out, target, field);
      return;
    }

    if (field.isPrimitiveAddress()) {
      writeGetter(out, target, field, primitiveAddressGetter(field, "ms"));
      writeSetter(out, target, field, true, """
          var address = allocator.allocate(<layout>);
          address.set(<layout>, 0L, value);
          FM$VH$<name>.set(ms, address);"""
          .replace("<layout>", field.valueLayout())
          .replace("<name>", name));
      return;
    }

    var typeElement = field.typeElement;

    if (isNestedValue(field)) {
      String fieldClassName = foreignMemoryClassName(typeElement);

      if (typeElement.getKind() == ElementKind.RECORD) {
        writeGetter(out, target, field, """
            <foreignClassName>.fromMemorySegment(ms.asSlice(
                FM$LAYOUT.byteOffset(FM$PE$<name>),
                FM$LAYOUT.select(FM$PE$<name>).byteSize()))"""
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));

        boolean needsAllocator = recordConverterNeedsAllocator(typeElement);
        writeSetter(out, target, field, needsAllocator, """
            var layout = FM$LAYOUT.select(FM$PE$<name>);
            var slice = ms.asSlice(
                FM$LAYOUT.byteOffset(FM$PE$<name>), layout.byteSize());
            <foreignClassName>.toMemorySegment(value, slice<allocator>);"""
            .replace("<allocator>", needsAllocator ? ", allocator" : "")
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));
      } else {
        writeGetter(out, target, field, """
            new <foreignClassName>(ms.asSlice(
                FM$LAYOUT.byteOffset(FM$PE$<name>),
                FM$LAYOUT.select(FM$PE$<name>).byteSize()))"""
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));

        writeSetter(out, target, field, false, """
            var layout = FM$LAYOUT.select(FM$PE$<name>);
            var slice = ms.asSlice(
                FM$LAYOUT.byteOffset(FM$PE$<name>), layout.byteSize());
            MemorySegment.copy(((<foreignClassName>)value).ms, 0,
                slice, 0, layout.byteSize());"""
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));
      }
    } else if (isNestedAddress(field)) {
      if (!target.isStatic()
          && typeElement.getKind() == ElementKind.RECORD) {
        reportMemoryBackedRecordAddressField(field);
        writeThrowingFieldAccessors(out, target.className(), field, false);
        return;
      }

      String fieldClassName = foreignMemoryClassName(typeElement);

      writeGetter(out, target, field, nestedAddressGetter(field, "ms"));

      if (typeElement.getKind() == ElementKind.RECORD) {
        writeSetter(out, target, field, true, """
            FM$VH$<name>.set(ms,
                <foreignClassName>.toMemorySegment(allocator, value));"""
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));
      } else {
        writeSetter(out, target, field, false,
            "FM$VH$<name>.set(ms, ((<fieldClass>) value).ms);"
                .replace("<fieldClass>", fieldClassName)
                .replace("<name>", name));
      }
    } else {
      writeGetter(out, target, field,
          "(<type>) FM$VH$<name>.get(ms)"
              .replace("<type>", field.typeName())
              .replace("<name>", name));

      writeSetter(out, target, field, false,
          "FM$VH$<name>.set(ms, value);".replace("<name>", name));
    }
  }

  private void writeGetter(Writer out, AccessorTarget target,
      VariableGenerator field, String expression) throws IOException {
    out.write("""

          <head> {
            return <expression>;
          }
        """
        .replace("<head>", target.getterHead(field))
        .replace("<expression>", expression.replace("\n", "\n    ")));
  }

  private void writeSetter(Writer out, AccessorTarget target,
      VariableGenerator field, boolean needsAllocator, String body)
      throws IOException {
    var statements = target.isStatic() ? body : body + "\nreturn this;";

    out.write("""

          <head> {
            <statements>
          }
        """
        .replace("<head>", target.setterHead(field, needsAllocator))
        .replace("<statements>", statements.replace("\n", "\n    ")));
  }

  private void writeThrowingAccessors(Writer out, AccessorTarget target,
      VariableGenerator field) throws IOException {
    if (target.isStatic()) {
      writeThrowingStaticAccessors(out, field);
    } else {
      writeThrowingFieldAccessors(out, target.className(), field, true);
    }
  }

  private void writeAccessorsBuffer(Writer out, VariableGenerator field)
      throws IOException {
    String type = field.elementTypeName();
    String capType = capitalize(type);
    long size = field.sequence;

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

          private java.nio.<Type>Buffer FM$BB$<name>;

          public java.nio.<Type>Buffer <name>() {
            if (FM$BB$<name> == null) {
              FM$BB$<name> = <name>$MemorySegment().asByteBuffer()
                  .order(java.nio.ByteOrder.nativeOrder())<buffer>;
            }
            return FM$BB$<name>;
          }

          /// Get element at index.
          public <type> <name>(int index) {
            return <name>$MemorySegment()
              .getAtIndex(ValueLayout.JAVA_<TYPE>, index);
          }

          /// Set element at index.
          public void <name>(int index, <type> value) {
            <name>$MemorySegment()
              .setAtIndex(ValueLayout.JAVA_<TYPE>, index, value);
          }

          /// Replace values from array.
          public void <name>(<type>[] value) {
            if (value.length != <size>) {
              throw new IllegalArgumentException();
            }
            MemorySegment.copy(value, 0,
                <name>$MemorySegment(), ValueLayout.JAVA_<TYPE>, 0, <size>);
          }
        """
        .replace("<name>", field.name())
        .replace("<Type>", capType)
        .replace("<type>", type)
        .replace("<TYPE>", type.toUpperCase(Locale.ROOT))
        .replace("<buffer>", type.equals("byte")
            ? "" : ".as" + capType + "Buffer()")
        .replace("<size>", Long.toString(size)));
  }

  private void validateFields(List<VariableGenerator> fields) {
    for (var field : fields) {
      if (field.hasSequenceOnUnsupportedType()) {
        processingEnv.getMessager().printError(
            "@Sequence is only supported on array and Buffer types",
            field.element);
        continue;
      }

      if (field.isString()) {
        processingEnv.getMessager().printError(
            "String fields are not supported on @Struct or @Union memory types",
            field.element);
        return;
      }
    }
  }

  private boolean fieldAccessorsShouldThrow(
      AccessorTarget target, VariableGenerator field) {
    return field.isString()
        || field.unsupported()
        || (target.isStatic() && field.isArrayOrBuffer());
  }

  private void reportMemoryBackedRecordAddressField(VariableGenerator field) {
    processingEnv.getMessager().printError(
        "@Address record fields are not supported on memory-backed "
            + "@Struct or @Union interfaces; use an interface struct or "
            + "MemorySegment for persistent pointer fields",
        field.element);
  }

  private void reportMemoryBackedPrimitiveAddressField(
      VariableGenerator field) {
    processingEnv.getMessager().printError(
        "@Address primitive fields are not supported on memory-backed "
            + "@Struct or @Union interfaces; use a memory-backed interface "
            + "type or MemorySegment for persistent pointer fields",
        field.element);
  }

  private boolean isNestedValue(VariableGenerator field) {
    return field.isForeignMemory() && field.isValue();
  }

  private boolean isNestedAddress(VariableGenerator field) {
    return field.isForeignMemory() && field.isAddress();
  }

  private boolean isRecordAddress(VariableGenerator variable) {
    return variable.isRecord() && variable.isAddress();
  }

  private boolean needsAllocatorWrite(VariableGenerator variable) {
    return variable.isPrimitiveAddress()
        || isRecordAddress(variable)
        || (variable.isRecord() && variable.isValue()
            && recordConverterNeedsAllocator(variable.typeElement));
  }

  private boolean recordConverterNeedsAllocator(TypeElement type) {
    return recordConverterNeedsAllocator(type, new HashSet<>());
  }

  private boolean recordConverterNeedsAllocator(
      TypeElement type, Set<String> visited) {
    if (type == null || type.getKind() != ElementKind.RECORD)
      return false;

    var name = type.getQualifiedName().toString();
    if (!visited.add(name))
      return false;

    for (var component : type.getRecordComponents()) {
      var componentGen = new VariableGenerator(processingEnv, component);

      if (componentGen.isPrimitiveAddress())
        return true;

      if (isRecordAddress(componentGen))
        return true;

      if (componentGen.isRecord() && componentGen.isValue()
          && recordConverterNeedsAllocator(componentGen.typeElement, visited))
        return true;
    }

    return false;
  }

  private String nestedAddressGetter(
      VariableGenerator field, String segment) {
    return foreignMemoryClassName(field.typeElement)
        + ".reinterpret((MemorySegment) FM$VH$" + field.name()
        + ".get(" + segment + ")" + ")";
  }

  private String primitiveAddressGetter(
      VariableGenerator field, String segment) {
    String layout = field.valueLayout();
    String address = "((MemorySegment) FM$VH$" + field.name()
        + ".get(" + segment + "))";

    return address + ".reinterpret(" + layout + ".byteSize())\n"
        + "    .get(" + layout + ", 0L)";
  }

  private String capitalize(String name) {
    return Character.toTitleCase(name.charAt(0)) + name.substring(1);
  }
}

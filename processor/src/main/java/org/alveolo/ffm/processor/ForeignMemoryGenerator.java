package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemorySimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/// Coordinates generation of one foreign-memory companion class.
final class ForeignMemoryGenerator {
  private final ProcessingEnvironment processingEnv;
  private final ForeignMemoryAnalyzer analyzer;
  private final ObjectMethodsGenerator objectGenerator;
  private final ForeignMemoryAccessorGenerator accessorGenerator;

  ForeignMemoryGenerator(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
    analyzer = new ForeignMemoryAnalyzer(processingEnv);
    objectGenerator = new ObjectMethodsGenerator(processingEnv);
    var indexedFieldGenerator = new IndexedFieldGenerator(
        processingEnv, analyzer);
    accessorGenerator = new ForeignMemoryAccessorGenerator(
        processingEnv, analyzer, indexedFieldGenerator);
  }

  void write(TypeElement source, String kind, boolean vtable)
      throws IOException {
    analyzer.validateRecordComponents(source);

    var isStructInterface = kind.equals("struct")
        && source.getKind() == ElementKind.INTERFACE;
    var objectMethods = isStructInterface
        ? objectGenerator.objectMethods(source)
        : ObjectMethodsGenerator.Methods.empty();
    if (!objectGenerator.validateObjectMethods(
        source, objectMethods, vtable))
      return;

    var preparedObjectMethods = objectGenerator.prepare(
        source, objectMethods);
    if (preparedObjectMethods == null) return;

    objectGenerator.writeDispatchTable(source, preparedObjectMethods);

    var fields = analyzer.inferFields(source, isStructInterface);
    analyzer.validateFields(fields);

    writeSource(source, kind, vtable, fields, preparedObjectMethods);
  }

  private void writeSource(TypeElement source, String kind, boolean vtable,
      ForeignMemoryAnalyzer.Fields fields,
      ObjectMethodsGenerator.Prepared objectMethods)
      throws IOException {
    var elements = processingEnv.getElementUtils();
    var packageName = packageName(source, elements);
    var sourceSimpleName = source.getSimpleName().toString();
    var className = ProcessorUtils.foreignMemoryClassName(source, elements);
    var simpleClassName = foreignMemorySimpleClassName(source);
    var vtableSimpleName = sourceSimpleName + "Vtbl";

    var file = processingEnv.getFiler().createSourceFile(className, source);
    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.append("package ").append(packageName).append(";\n\n");
      }

      var declaration = switch (source.getKind()) {
        case INTERFACE -> simpleClassName + " implements "
            + sourceSimpleName;
        case RECORD -> simpleClassName;
        case ElementKind unexpected -> throw new IllegalArgumentException(
            "Unexpected value: " + unexpected);
      };

      out.write("""
          import java.lang.foreign.*;
          <methodHandleImport>
          @javax.annotation.processing.Generated(
              "<generator>")
          public final class <declaration> {
          """
          .replace("<generator>",
              ForeignMemoryProcessor.class.getCanonicalName())
          .replace("<declaration>", declaration)
          .replace("<methodHandleImport>", objectMethods.hasSymbolMethods()
              ? "import java.lang.invoke.MethodHandle;\n" : ""));

      writeLayout(out, fields, kind, vtable);
      if (vtable) {
        objectGenerator.writeVtableMetadata(out);
      }
      writeAllocators(out);
      writeReinterprets(out, source, simpleClassName);
      writeArrayElementHelpers(out, source, simpleClassName);

      switch (source.getKind()) {
        case INTERFACE -> {
          writeConstructors(out, simpleClassName, vtableSimpleName,
              objectMethods.hasVirtualMethods());
          accessorGenerator.writeInterfaceFields(
              out, simpleClassName, fields);
          objectGenerator.writeSymbolHolder(out, objectMethods);
          objectGenerator.writeObjectMethods(out, objectMethods);
        }
        case RECORD -> {
          accessorGenerator.writeRecordConverters(
              out, sourceSimpleName, fields);
          accessorGenerator.writeRecordFields(out, fields);
        }
        case ElementKind unexpected -> throw new IllegalArgumentException(
            "Unexpected value: " + unexpected);
      }

      out.write("}\n");
    }
  }

  private void writeLayout(Writer out, ForeignMemoryAnalyzer.Fields fields,
      String kind, boolean vtable) throws IOException {
    out.write("""
          public static final MemoryLayout FM$LAYOUT =
              MemoryLayout.<kind>Layout(
                  org.alveolo.ffm.ForeignUtils.<kind>Pad(new MemoryLayout [] {
        """
        .replace("<kind>", kind));

    if (vtable) {
      out.write("        ValueLayout.ADDRESS.withName(\""
          + ObjectMethodsGenerator.VTABLE_FIELD + "\"),\n");
    }

    var layoutFields = fields.fields().stream()
        .map(field -> {
          var indexed = fields.indexedFields().get(field.name());
          return new MemoryLayoutGenerator.LayoutField(field.name(),
              indexed == null ? field.layout() : indexed.layout(),
              field.unsupported(), field.typeName(), field.element);
        })
        .toList();

    out.write(new MemoryLayoutGenerator(processingEnv, layoutFields).layout());
    out.write("      }));\n");
  }

  private void writeAllocators(Writer out) throws IOException {
    out.write("""

          public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(
              FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
          }

          public static MemorySegment allocate(
              SegmentAllocator allocator, long count) {
            if (count < 0) {
              throw new IllegalArgumentException("count must be non-negative");
            }
            return allocator.allocate(FM$LAYOUT, count);
          }
        """);
  }

  private void writeReinterprets(
      Writer out, TypeElement source, String simpleClassName)
      throws IOException {
    var isRecord = source.getKind() == ElementKind.RECORD;
    var returnType = isRecord
        ? source.getSimpleName().toString() : simpleClassName;
    var expression = isRecord
        ? "fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()))"
        : "new " + simpleClassName + "(ms.reinterpret(FM$LAYOUT.byteSize()))";

    out.write("""

          public static <type> reinterpret(MemorySegment ms) {
            return <expression>;
          }

          public static MemorySegment reinterpret(
              MemorySegment ms, long count) {
            if (count < 0) {
              throw new IllegalArgumentException("count must be non-negative");
            }
            return ms.reinterpret(Math.multiplyExact(
                FM$LAYOUT.byteSize(), count));
          }
        """
        .replace("<type>", returnType)
        .replace("<expression>", expression));
  }

  private void writeArrayElementHelpers(
      Writer out, TypeElement source, String simpleClassName)
      throws IOException {
    out.write("""

          private static MemorySegment FM$at(MemorySegment array, long index) {
            if (index < 0) {
              throw new IndexOutOfBoundsException(index);
            }
            return array.asSlice(Math.multiplyExact(
                index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
          }
        """);

    if (source.getKind() == ElementKind.RECORD) {
      out.write("""

            public static <source> at(MemorySegment array, long index) {
              return fromMemorySegment(FM$at(array, index));
            }
          """
          .replace("<source>", source.getSimpleName().toString()));
      return;
    }

    out.write("""

          public static <class> at(MemorySegment array, long index) {
            return new <class>(FM$at(array, index));
          }
        """
        .replace("<class>", simpleClassName));
  }

  private void writeConstructors(Writer out, String className,
      String vtableTypeName, boolean hasVirtualMethods)
      throws IOException {
    out.write("""

          public final MemorySegment ms;
        """);

    if (hasVirtualMethods) {
      out.write("""

            private final <vtableType> ff$vtbl;
          """
          .replace("<vtableType>", vtableTypeName));
    }

    var vtableInitializer = hasVirtualMethods
        ? "    this.ff$vtbl = " + vtableTypeName
            + "FD.reinterpret((MemorySegment) FM$VH$ff$vtbl.get(ms));\n"
        : "";
    out.write("""

          public <class>(SegmentAllocator allocator) {
            this(allocate(allocator));
          }

          public <class>(MemorySegment ms) {
            this.ms = ms;
            <vtableInitializer>
          }
        """
        .replace("<class>", className)
        .replace("    <vtableInitializer>\n", vtableInitializer));

    if (hasVirtualMethods) {
      out.write("""

            private <vtableType> ff$vtbl() {
              return ff$vtbl;
            }
          """
          .replace("<vtableType>", vtableTypeName));
    }
  }
}

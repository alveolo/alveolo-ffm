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

  ForeignMemoryGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes) {
    this.processingEnv = processingEnv;
    analyzer = new ForeignMemoryAnalyzer(processingEnv, generatedTypes);
    objectGenerator = new ObjectMethodsGenerator(processingEnv, generatedTypes);
    var indexedFieldGenerator = new IndexedFieldGenerator(analyzer);
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
    var vtableSimpleName =
        ProcessorUtils.vtableImplementationSimpleClassName(source);

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
          @javax.annotation.processing.Generated(
              "<generator>")
          public final class <declaration> {
          """
          .replace("<generator>",
              ForeignMemoryProcessor.class.getCanonicalName())
          .replace("<declaration>", declaration));

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
              objectMethods.hasUsableVirtualMethods());
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
          public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
              java.lang.foreign.MemoryLayout.<kind>Layout(
                  org.alveolo.ffm.ForeignUtils.<kind>Pad(
                      new java.lang.foreign.MemoryLayout [] {
        """
        .replace("<kind>", kind));

    if (vtable) {
      out.write("        java.lang.foreign.ValueLayout.ADDRESS.withName(\""
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

          public static java.lang.foreign.MemorySegment allocate$F(
              java.lang.foreign.SegmentAllocator allocator$f) {
            return allocator$f.allocate(
              MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
          }

          public static java.lang.foreign.MemorySegment allocate$F(
              java.lang.foreign.SegmentAllocator allocator$f, long count$f) {
            if (count$f < 0) {
              throw new IllegalArgumentException("count must be non-negative");
            }
            return allocator$f.allocate(MemoryLayout$F, count$f);
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
        ? "fromMemorySegment$F(memorySegment$f.reinterpret("
            + "MemoryLayout$F.byteSize()))"
        : "new " + simpleClassName + "(memorySegment$f.reinterpret("
            + "MemoryLayout$F.byteSize()))";

    out.write("""

          public static <type> reinterpret$F(
              java.lang.foreign.MemorySegment memorySegment$f) {
            return <expression>;
          }

          public static java.lang.foreign.MemorySegment reinterpret$F(
              java.lang.foreign.MemorySegment memorySegment$f, long count$f) {
            if (count$f < 0) {
              throw new IllegalArgumentException("count must be non-negative");
            }
            return memorySegment$f.reinterpret(Math.multiplyExact(
                MemoryLayout$F.byteSize(), count$f));
          }
        """
        .replace("<type>", returnType)
        .replace("<expression>", expression));
  }

  private void writeArrayElementHelpers(
      Writer out, TypeElement source, String simpleClassName)
      throws IOException {
    out.write("""

          private static java.lang.foreign.MemorySegment elementAt$F(
              java.lang.foreign.MemorySegment array$f, long index$f) {
            if (index$f < 0) {
              throw new IndexOutOfBoundsException(index$f);
            }
            return array$f.asSlice(Math.multiplyExact(
                index$f, MemoryLayout$F.byteSize()), MemoryLayout$F.byteSize());
          }
        """);

    if (source.getKind() == ElementKind.RECORD) {
      out.write("""

            public static <source> at$F(
                java.lang.foreign.MemorySegment array$f, long index$f) {
              return fromMemorySegment$F(elementAt$F(array$f, index$f));
            }
          """
          .replace("<source>", source.getSimpleName().toString()));
      return;
    }

    out.write("""

          public static <class> at$F(
              java.lang.foreign.MemorySegment array$f, long index$f) {
            return new <class>(elementAt$F(array$f, index$f));
          }
        """
        .replace("<class>", simpleClassName));
  }

  private void writeConstructors(Writer out, String className,
      String vtableTypeName, boolean hasVirtualMethods)
      throws IOException {
    out.write("""

          public final java.lang.foreign.MemorySegment MemorySegment$F;
        """);

    if (hasVirtualMethods) {
      out.write("""

            private final <vtableType> Vtable$F;
          """
          .replace("<vtableType>", vtableTypeName));
    }

    var vtableInitializer = hasVirtualMethods
        ? "    this.Vtable$F = " + vtableTypeName
            + ".reinterpret$F((java.lang.foreign.MemorySegment) "
            + "vtable$F$VarHandle$F.get(MemorySegment$F));\n"
        : "";
    out.write("""

          public <class>(java.lang.foreign.SegmentAllocator allocator$f) {
            this(allocate$F(allocator$f));
          }

          public <class>(java.lang.foreign.MemorySegment memorySegment$f) {
            this.MemorySegment$F = memorySegment$f;
            <vtableInitializer>
          }
        """
        .replace("<class>", className)
        .replace("    <vtableInitializer>\n", vtableInitializer));

    if (hasVirtualMethods) {
      out.write("""

            private <vtableType> Vtable$F() {
              return Vtable$F;
            }
          """
          .replace("<vtableType>", vtableTypeName));
    }
  }
}

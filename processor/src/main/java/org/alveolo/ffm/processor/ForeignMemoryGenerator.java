package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemoryClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemorySimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.vtableImplementationSimpleClassName;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/// Coordinates generation of one foreign-memory companion class.
final class ForeignMemoryGenerator {
  private final ProcessingEnvironment processingEnv;
  private final ForeignMemoryAnalyzer analyzer;
  private final ObjectMethodsGenerator objectGenerator;
  private final ForeignMemoryAccessorGenerator accessorGenerator;
  private final LayoutCycleValidator layoutCycles;

  ForeignMemoryGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes, LayoutCycleValidator layoutCycles) {
    this.processingEnv = processingEnv;
    this.layoutCycles = layoutCycles;
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
    var model = isStructInterface
        ? new StructInterfaceModel(processingEnv).analyze(source) : null;
    if (model != null && !model.valid()) return;

    var objectMethods = model == null
        ? ObjectMethodsGenerator.Methods.empty() : model.objectMethods();
    var effectiveVtable = model == null ? vtable : model.vtable();
    if (!objectGenerator.validateObjectMethods(
        source, objectMethods, effectiveVtable))
      return;

    var preparedObjectMethods = objectGenerator.prepare(
        source, objectMethods);
    if (preparedObjectMethods == null) return;

    objectGenerator.writeDispatchTable(source, preparedObjectMethods);

    var fields = model == null
        ? analyzer.inferFields(source, isStructInterface)
        : analyzer.inferFields(model.fieldMethods());
    analyzer.validateFields(fields);
    var baseFields = model == null || model.baseStruct() == null
        ? null : analyzer.inferFields(model.baseFieldMethods());
    if (baseFields != null) {
      analyzer.validateFields(baseFields);
    }

    writeSource(source, kind, effectiveVtable, fields,
        baseFields, preparedObjectMethods,
        model == null ? null : model.baseStruct());
  }

  private void writeSource(TypeElement source, String kind, boolean vtable,
      ForeignMemoryAnalyzer.Fields fields,
      ForeignMemoryAnalyzer.Fields baseFields,
      ObjectMethodsGenerator.Prepared objectMethods,
      TypeElement baseStruct)
      throws IOException {
    var elements = processingEnv.getElementUtils();
    var packageName = packageName(source, elements);
    var sourceSimpleName = source.getSimpleName().toString();
    var className = foreignMemoryClassName(source, elements);
    var simpleClassName = foreignMemorySimpleClassName(source);
    var vtableSimpleName = vtableImplementationSimpleClassName(source);
    var baseClassName = baseStruct == null ? null
        : foreignMemoryClassName(baseStruct, elements);

    var dependencies = new ArrayList<LayoutCycleValidator.Dependency>();
    if (baseClassName != null) {
      dependencies.add(
          new LayoutCycleValidator.Dependency(baseClassName, source));
    }
    for (var field : fields.fields()) {
      if (field.foreignMemory && field.isValue() && !field.unsupported()) {
        dependencies.add(new LayoutCycleValidator.Dependency(
            field.foreignMemoryClassName(), field.element));
      }
    }
    layoutCycles.dependencies.put(className, List.copyOf(dependencies));

    var file = processingEnv.getFiler().createSourceFile(className, source);
    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.append("package ").append(packageName).append(";\n\n");
      }

      var declaration = switch (source.getKind()) {
        case INTERFACE -> simpleClassName
            + (baseClassName == null ? "" : " extends " + baseClassName)
            + " implements " + sourceSimpleName;
        case RECORD -> simpleClassName;
        case ElementKind unexpected -> throw new IllegalArgumentException(
            "Unexpected value: " + unexpected);
      };

      out.write("""
          @javax.annotation.processing.Generated(
              "<generator>")
          <modifier> class <declaration> {
          """
          .replace("<generator>",
              ForeignMemoryProcessor.class.getCanonicalName())
          .replace("<modifier>", source.getKind() == ElementKind.INTERFACE
              && kind.equals("struct") ? "public" : "public final")
          .replace("<declaration>", declaration));

      writeLayout(out, fields, kind, vtable && baseStruct == null,
          baseClassName);
      if (vtable && baseStruct == null) {
        objectGenerator.writeVtableMetadata(out);
      }
      if (!vtable) {
        writeAllocators(out);
      }
      writeReinterprets(out, source, simpleClassName);
      writeArrayElementHelpers(out, source, simpleClassName);

      switch (source.getKind()) {
        case INTERFACE -> {
          writeConstructors(out, simpleClassName, vtableSimpleName,
              vtable, objectMethods.hasUsableVirtualMethods(), baseClassName);
          if (baseFields != null) {
            accessorGenerator.writeInheritedFluentSetters(
                out, simpleClassName, baseFields);
          }
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
      String kind, boolean vtable, String baseClassName) throws IOException {
    out.write("""
          public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
              java.lang.foreign.MemoryLayout.<kind>Layout(
                  org.alveolo.ffm.ForeignUtils.<kind>Pad(
                      new java.lang.foreign.MemoryLayout [] {
        """
        .replace("<kind>", kind));

    if (baseClassName != null) {
      out.write("        " + baseClassName + ".MemoryLayout$F,\n");
    }

    if (vtable) {
      out.write("        java.lang.foreign.ValueLayout.ADDRESS.withName(\""
          + ObjectMethodsGenerator.VTABLE_FIELD + "\"),\n");
    }

    var layoutFields = fields.fields().stream()
        .map(field -> {
          var indexed = fields.indexedFields().get(field.name());
          return new MemoryLayoutGenerator.LayoutField(field.name(),
              indexed == null ? field.layout() : indexed.layout(),
              field.unsupported() && field.canonicalScalarError() == null,
              field.typeName(), field.element);
        })
        .toList();

    out.write(new MemoryLayoutGenerator(processingEnv, layoutFields).layout());
    out.write("      }));\n");
  }

  private void writeAllocators(Writer out) throws IOException {
    out.write("""

          public static java.lang.foreign.MemorySegment allocate$F(
              java.lang.foreign.SegmentAllocator allocator) {
            return allocator.allocate(
              MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
          }

          public static java.lang.foreign.MemorySegment allocate$F(
              java.lang.foreign.SegmentAllocator allocator, long count) {
            if (count < 0) {
              throw new IllegalArgumentException("count must be non-negative");
            }
            return allocator.allocate(MemoryLayout$F, count);
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
        ? "fromMemorySegment$F(memorySegment.reinterpret("
            + "MemoryLayout$F.byteSize()))"
        : "new " + simpleClassName + "(memorySegment.reinterpret("
            + "MemoryLayout$F.byteSize()))";

    out.write("""

          public static <type> reinterpret$F(
              java.lang.foreign.MemorySegment memorySegment) {
            return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
                ? null : <expression>;
          }

          public static java.lang.foreign.MemorySegment reinterpret$F(
              java.lang.foreign.MemorySegment memorySegment, long count) {
            if (count < 0) {
              throw new IllegalArgumentException("count must be non-negative");
            }
            return memorySegment.reinterpret(Math.multiplyExact(
                MemoryLayout$F.byteSize(), count));
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
              java.lang.foreign.MemorySegment array, long index) {
            if (index < 0) {
              throw new IndexOutOfBoundsException(index);
            }
            return array.asSlice(Math.multiplyExact(
                index, MemoryLayout$F.byteSize()), MemoryLayout$F.byteSize());
          }
        """);

    if (source.getKind() == ElementKind.RECORD) {
      out.write("""

            public static <source> at$F(
                java.lang.foreign.MemorySegment array, long index) {
              return fromMemorySegment$F(elementAt$F(array, index));
            }
          """
          .replace("<source>", source.getSimpleName().toString()));
      return;
    }

    out.write("""

          public static <class> at$F(
              java.lang.foreign.MemorySegment array, long index) {
            return new <class>(elementAt$F(array, index));
          }
        """
        .replace("<class>", simpleClassName));
  }

  private void writeConstructors(Writer out, String className,
      String vtableTypeName, boolean vtable, boolean hasVirtualMethods,
      String baseClassName)
      throws IOException {
    if (baseClassName == null) {
      out.write("""

            public final java.lang.foreign.MemorySegment MemorySegment$F;
          """);
    }

    if (hasVirtualMethods) {
      out.write("""

            private final <vtableType> Vtable$F;
          """
          .replace("<vtableType>", vtableTypeName));
    }

    if (!vtable) {
      out.write("""

            public <class>(java.lang.foreign.SegmentAllocator allocator) {
              this(allocate$F(allocator));
            }
          """.replace("<class>", className));
    }

    var memoryInitializer = baseClassName == null
        ? "this.MemorySegment$F = memorySegment;"
        : "super(memorySegment);";
    out.write("""

          public <class>(java.lang.foreign.MemorySegment memorySegment) {
            <memoryInitializer>
        """
        .replace("<class>", className)
        .replace("<memoryInitializer>", memoryInitializer));

    if (vtable && (baseClassName == null || hasVirtualMethods)) {
      out.write("""
              var vtable$f = (java.lang.foreign.MemorySegment)
                  vtable$F$VarHandle$F.get(MemorySegment$F);
              if (vtable$f.equals(java.lang.foreign.MemorySegment.NULL)) {
                throw new IllegalArgumentException("Object has a NULL vtable");
              }
          """);
    }
    if (hasVirtualMethods) {
      out.write("""
              this.Vtable$F = <vtableType>.reinterpret$F(vtable$f);
          """.replace("<vtableType>", vtableTypeName));
    }
    out.write("  }\n");

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

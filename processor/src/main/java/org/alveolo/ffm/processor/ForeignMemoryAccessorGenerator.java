package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static org.alveolo.ffm.processor.ProcessorUtils.sourceMethodSignature;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;

/// Generates field metadata, field accessors, and record converters.
///
/// Ordinary and indexed field declarations are emitted as complete blocks in
/// source declaration order. Indexed field details remain delegated
/// to [IndexedFieldGenerator].
final class ForeignMemoryAccessorGenerator {
  private final Messager messager;
  private final ForeignMemoryAnalyzer analyzer;
  private final IndexedFieldGenerator indexedFieldGenerator;

  ForeignMemoryAccessorGenerator(ProcessingEnvironment processingEnv,
      ForeignMemoryAnalyzer analyzer,
      IndexedFieldGenerator indexedFieldGenerator) {
    messager = processingEnv.getMessager();
    this.analyzer = analyzer;
    this.indexedFieldGenerator = indexedFieldGenerator;
  }

  void writeInterfaceFields(Writer out, String className,
      ForeignMemoryAnalyzer.Fields fields) throws IOException {
    var target = AccessorTarget.fluent(className);
    for (var field : fields.fields()) {
      var indexed = fields.indexedFields().get(field.name());
      if (indexed != null) {
        indexedFieldGenerator.writeMetadata(out, indexed);
        indexedFieldGenerator.writeVarHandle(out, indexed);
        indexedFieldGenerator.writeInstanceAccessors(
            out, className, indexed);
        continue;
      }

      writeMetadata(out, field);
      writeVarHandle(out, field);
      if (field.isNioBuffer()) {
        writeThrowingFieldAccessors(out, className, field, false);
      } else {
        writeAccessors(out, target, field);
      }
    }

    writeUnsupportedMethods(out, fields);
  }

  void writeRecordFields(Writer out, ForeignMemoryAnalyzer.Fields fields)
      throws IOException {
    for (var field : fields.fields()) {
      var indexed = fields.indexedFields().get(field.name());
      if (indexed != null) {
        indexedFieldGenerator.writeMetadata(out, indexed);
        indexedFieldGenerator.writeVarHandle(out, indexed);
        indexedFieldGenerator.writeStaticAccessors(out, indexed);
        continue;
      }

      writeMetadata(out, field);
      writeVarHandle(out, field);
      writeAccessors(out, AccessorTarget.STATIC, field);
    }
  }

  void writeRecordConverters(Writer out, String sourceClassName,
      ForeignMemoryAnalyzer.Fields fields) throws IOException {
    var variables = fields.fields();
    var needsAllocator = variables.stream()
        .anyMatch(analyzer::needsAllocatorWrite);

    var fromMemorySegmentFields = variables.stream()
        .map(field -> field.name() + "(memorySegment)")
        .collect(joining(",\n        "));

    var toMemorySegmentFields = recordFieldWrites(fields, needsAllocator)
        .indent(2);

    var toMemorySegmentTemplate = needsAllocator
        ? """
            public static void toMemorySegment$F(
                <source> source,
                java.lang.foreign.MemorySegment memorySegment,
                java.lang.foreign.SegmentAllocator allocator) {
              <toMemorySegmentFields>
            }
            """
        : """
            public static void toMemorySegment$F(
                <source> source,
                java.lang.foreign.MemorySegment memorySegment) {
              <toMemorySegmentFields>
            }
            """;
    var toMemorySegmentMethod = toMemorySegmentTemplate
        .replace("<source>", sourceClassName)
        .replace("  <toMemorySegmentFields>\n", toMemorySegmentFields)
        .indent(2)
        .stripTrailing();

    out.write("""

        <toMemorySegmentMethod>

          public static java.lang.foreign.MemorySegment toMemorySegment$F(
              java.lang.foreign.SegmentAllocator allocator,
              <source> source) {
            var memorySegment = allocate$F(allocator);
            <toMemorySegment>
            return memorySegment;
          }

          public static <source> fromMemorySegment$F(
              java.lang.foreign.MemorySegment memorySegment) {
            return new <source>(
                <fromMemorySegmentFields>);
          }
        """
        .replace("<toMemorySegmentMethod>", toMemorySegmentMethod)
        .replace("<source>", sourceClassName)
        .replace("<toMemorySegment>", needsAllocator
            ? "toMemorySegment$F(source, memorySegment, allocator);"
            : "toMemorySegment$F(source, memorySegment);")
        .replace("<fromMemorySegmentFields>", fromMemorySegmentFields));
  }

  private void writeMetadata(Writer out, VariableGenerator field)
      throws IOException {
    out.write("""

          public static final java.lang.foreign.MemoryLayout.PathElement
              <name>$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
                  .groupElement("<name>");
        """
        .replace("<name>", field.name()));
  }

  private void writeVarHandle(Writer out, VariableGenerator field)
      throws IOException {
    if (field.isNioBuffer()
        || (field.isForeignMemory() && field.isValue()))
      return;

    var initializer = field.sequence > 1
        ? "MemoryLayout$F.varHandle(<name>$PathElement$F);"
        : """
            java.lang.invoke.MethodHandles.insertCoordinates(
                MemoryLayout$F.varHandle(<name>$PathElement$F), 1, 0L);
            """
            .stripTrailing();

    out.write("""

          public static final java.lang.invoke.VarHandle <name>$VarHandle$F =
              <initializer>
        """
        .replace("<initializer>", initializer.replace("\n", "\n      "))
        .replace("<name>", field.name()));
  }

  private String recordFieldWrites(
      ForeignMemoryAnalyzer.Fields fields, boolean withAllocator) {
    return fields.fields().stream()
        .map(field -> recordFieldWrite(field,
            fields.indexedFields().get(field.name()), withAllocator))
        .collect(joining("\n"));
  }

  private String recordFieldWrite(VariableGenerator field,
      IndexedField indexed, boolean withAllocator) {
    var name = field.name();
    var needsAllocator = withAllocator && (indexed == null
        ? analyzer.needsAllocatorWrite(field)
        : analyzer.indexedArrayNeedsAllocator(indexed));

    return "<name>(memorySegment, <allocator>source.<name>());"
        .replace("<name>", name)
        .replace("<allocator>", needsAllocator ? "allocator, " : "");
  }

  private void writeUnsupportedMethods(Writer out,
      ForeignMemoryAnalyzer.Fields fields) throws IOException {
    for (var method : fields.unsupportedMethods()) {
      out.write("""

            <signature> {
              throw new RuntimeException("Check compile errors!");
            }
          """
          .replace("<signature>", sourceMethodSignature(method)));
    }
  }

  /// Receiver style for generated field accessors: records use static accessors
  /// over an explicit MemorySegment parameter; memory-backed interfaces use
  /// instance accessors over `this.MemorySegment$F` with fluent setters.
  private record AccessorTarget(boolean isStatic, String className) {
    static final AccessorTarget STATIC = new AccessorTarget(true, null);

    static AccessorTarget fluent(String className) {
      return new AccessorTarget(false, className);
    }

    String getterHead(VariableGenerator field) {
      return (isStatic
          ? "public static <type> <name>("
              + "java.lang.foreign.MemorySegment memorySegment)"
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
              + "      java.lang.foreign.MemorySegment memorySegment, "
              + "java.lang.foreign.SegmentAllocator allocator,"
              + " <type> value)"
          : "public static void <name>("
              + "java.lang.foreign.MemorySegment memorySegment, "
              + "<type> value)";
    }

    private String fluentSetterHead(boolean needsAllocator) {
      return needsAllocator
          ? "public <class> <name>(\n"
              + "      java.lang.foreign.SegmentAllocator allocator, "
              + "<type> value)"
          : "public <class> <name>(<type> value)";
    }
  }

  private void writeAccessors(Writer out, AccessorTarget target,
      VariableGenerator field) throws IOException {
    var name = field.name();
    var segment = target.isStatic() ? "memorySegment" : "MemorySegment$F";

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
      writeGetter(out, target, field, primitiveAddressGetter(field, segment));
      writeSetter(out, target, field, true, """
          var address = allocator.allocate(<layout>);
          address.set(<layout>, 0L, value);
          <name>$VarHandle$F.set(<segment>, address);
          """
          .stripTrailing()
          .replace("<layout>", field.valueLayout())
          .replace("<segment>", segment)
          .replace("<name>", name));
      return;
    }

    var typeElement = field.typeElement;
    if (isNestedValue(field)) {
      var fieldClassName = field.foreignMemoryClassName();

      if (typeElement.getKind() == ElementKind.RECORD) {
        writeGetter(out, target, field, """
            <foreignClassName>.fromMemorySegment$F(<segment>.asSlice(
                MemoryLayout$F.byteOffset(<name>$PathElement$F),
                MemoryLayout$F.select(<name>$PathElement$F).byteSize()))
            """
            .stripTrailing()
            .replace("<foreignClassName>", fieldClassName)
            .replace("<segment>", segment)
            .replace("<name>", name));

        var needsAllocator = analyzer.recordConverterNeedsAllocator(
            typeElement);
        writeSetter(out, target, field, needsAllocator, """
            var memoryLayout =
                MemoryLayout$F.select(<name>$PathElement$F);
            var slice = <segment>.asSlice(
                MemoryLayout$F.byteOffset(<name>$PathElement$F),
                memoryLayout.byteSize());
            <foreignClassName>.toMemorySegment$F(
                value, slice<allocator>);
            """
            .stripTrailing()
            .replace("<allocator>", needsAllocator ? ", allocator" : "")
            .replace("<foreignClassName>", fieldClassName)
            .replace("<segment>", segment)
            .replace("<name>", name));
      } else {
        writeGetter(out, target, field, """
            new <foreignClassName>(<segment>.asSlice(
                MemoryLayout$F.byteOffset(<name>$PathElement$F),
                MemoryLayout$F.select(<name>$PathElement$F).byteSize()))
            """
            .stripTrailing()
            .replace("<foreignClassName>", fieldClassName)
            .replace("<segment>", segment)
            .replace("<name>", name));

        writeSetter(out, target, field, false, """
            var memoryLayout =
                MemoryLayout$F.select(<name>$PathElement$F);
            var slice = <segment>.asSlice(
                MemoryLayout$F.byteOffset(<name>$PathElement$F),
                memoryLayout.byteSize());
            java.lang.foreign.MemorySegment.copy(
                <sourceSegment>, 0,
                slice, 0, memoryLayout.byteSize());
            """
            .stripTrailing()
            .replace("<sourceSegment>", nestedMemorySegment(field))
            .replace("<segment>", segment)
            .replace("<name>", name));
      }
      return;
    }

    if (isNestedAddress(field)) {
      if (!target.isStatic()
          && typeElement.getKind() == ElementKind.RECORD) {
        reportMemoryBackedRecordAddressField(field);
        writeThrowingFieldAccessors(out, target.className(), field, false);
        return;
      }

      writeGetter(out, target, field, nestedAddressGetter(field, segment));

      if (typeElement.getKind() == ElementKind.RECORD) {
        writeSetter(out, target, field, true, """
            <name>$VarHandle$F.set(<segment>,
                <foreignClassName>.toMemorySegment$F(allocator, value));
            """
            .stripTrailing()
            .replace("<foreignClassName>", field.foreignMemoryClassName())
            .replace("<segment>", segment)
            .replace("<name>", name));
      } else {
        writeSetter(out, target, field, false,
            "<name>$VarHandle$F.set(<segment>, <sourceSegment>);"
                .replace("<sourceSegment>",
                    nestedMemorySegment(field))
                .replace("<segment>", segment)
                .replace("<name>", name));
      }
      return;
    }

    writeGetter(out, target, field,
        "(<type>) <name>$VarHandle$F.get(<segment>)"
            .replace("<type>", field.typeName())
            .replace("<segment>", segment)
            .replace("<name>", name));
    writeSetter(out, target, field, false,
        "<name>$VarHandle$F.set(<segment>, value);"
            .replace("<segment>", segment)
            .replace("<name>", name));
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

  private void writeThrowingStaticAccessors(
      Writer out, VariableGenerator field) throws IOException {
    var name = field.name();
    var type = field.typeName();

    out.write("""

          public static <type> <name>(
              java.lang.foreign.MemorySegment memorySegment) {
            throw new RuntimeException("Check compile errors!");
          }

          public static void <name>(
              java.lang.foreign.MemorySegment memorySegment,
              <type> value) {
            throw new RuntimeException("Check compile errors!");
          }

          public static void <name>(
              java.lang.foreign.MemorySegment memorySegment,
              java.lang.foreign.SegmentAllocator allocator,
              <type> value) {
            throw new RuntimeException("Check compile errors!");
          }
        """
        .replace("<name>", name)
        .replace("<type>", type));
  }

  private void writeThrowingFieldAccessors(Writer out, String className,
      VariableGenerator field, boolean writeSetter) throws IOException {
    var name = field.name();
    var type = field.typeName();
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

  private boolean fieldAccessorsShouldThrow(
      AccessorTarget target, VariableGenerator field) {
    return field.isString()
        || field.unsupported()
        || (target.isStatic() && field.isArrayOrBuffer());
  }

  private void reportMemoryBackedRecordAddressField(
      VariableGenerator field) {
    messager.printError(
        "@Address record fields are not supported on memory-backed "
            + "@Struct or @Union interfaces; use an interface struct or "
            + "MemorySegment for persistent pointer fields",
        field.element);
  }

  private void reportMemoryBackedPrimitiveAddressField(
      VariableGenerator field) {
    messager.printError(
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

  private String nestedAddressGetter(
      VariableGenerator field, String segment) {
    return field.foreignMemoryClassName()
        + ".reinterpret$F((java.lang.foreign.MemorySegment) "
        + field.name() + "$VarHandle$F"
        + ".get(" + segment + "))";
  }

  private String primitiveAddressGetter(
      VariableGenerator field, String segment) {
    var layout = field.valueLayout();
    var address = "((java.lang.foreign.MemorySegment) " + field.name()
        + "$VarHandle$F"
        + ".get(" + segment + "))";

    return address + ".reinterpret(" + layout + ".byteSize())\n"
        + "    .get(" + layout + ", 0L)";
  }

  private String nestedMemorySegment(VariableGenerator field) {
    return field.isForeignMemoryImplementation()
        ? "value.MemorySegment$F"
        : "((" + field.foreignMemoryClassName() + ") value"
            + ").MemorySegment$F";
  }
}

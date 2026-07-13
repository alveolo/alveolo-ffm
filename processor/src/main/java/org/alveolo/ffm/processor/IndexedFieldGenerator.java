package org.alveolo.ffm.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.stream.IntStream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;

/// Generates the metadata and accessors for one inline indexed field.
///
/// Each writing entry point emits one complete section of the field so the
/// enclosing memory generator can keep all field-specific declarations
/// close together.
final class IndexedFieldGenerator {
  private final Elements elements;
  private final ForeignMemoryAnalyzer analyzer;

  IndexedFieldGenerator(
      ProcessingEnvironment processingEnv, ForeignMemoryAnalyzer analyzer) {
    this.elements = processingEnv.getElementUtils();
    this.analyzer = analyzer;
  }

  void writeMetadata(Writer out, IndexedField indexed) throws IOException {
    var name = indexed.element().name();

    out.write("""

          public static final java.lang.foreign.MemoryLayout.PathElement
              FM$PE$<name> = java.lang.foreign.MemoryLayout.PathElement
                  .groupElement("<name>");
        """
        .replace("<name>", name));

    for (var i = 0; i < indexed.dimensions().size(); i++) {
      out.write("""

            public static final java.lang.foreign.MemoryLayout.PathElement
                FM$PE$<name>$<index> =
                    java.lang.foreign.MemoryLayout.PathElement
                        .sequenceElement();
          """
          .replace("<name>", name)
          .replace("<index>", Integer.toString(i)));
    }

    out.write("""

          public static final java.lang.foreign.MemoryLayout FM$LAYOUT$<name> =
              FM$LAYOUT.select(FM$PE$<name>);

          public static final java.lang.foreign.MemoryLayout
              FM$ELEMENT_LAYOUT$<name> =
              <elementLayout>;

          public static final long FM$OFFSET$<name> =
              FM$LAYOUT.byteOffset(FM$PE$<name>);

          public static final long FM$SIZE$<name> =
              FM$LAYOUT$<name>.byteSize();
        """
        .replace("<name>", name)
        .replace("<elementLayout>", indexed.element().layout()));

    for (var i = 0; i < indexed.dimensions().size(); i++) {
      out.write("""

            public static final long FM$DIMENSION$<name>$<index> = <size>L;
          """
          .replace("<name>", name)
          .replace("<index>", Integer.toString(i))
          .replace("<size>", Long.toString(
              indexed.dimensions().get(i).size())));
    }
  }

  void writeVarHandle(Writer out, IndexedField indexed) throws IOException {
    if (indexed.structuredValueElement()) return;

    var name = indexed.element().name();
    var paths = IntStream.range(0, indexed.dimensions().size())
        .mapToObj(i -> "FM$PE$" + name + "$" + i)
        .toList();

    out.write("""

          public static final java.lang.invoke.VarHandle FM$VH$<name> =
              java.lang.invoke.MethodHandles.insertCoordinates(
                  FM$LAYOUT.varHandle(FM$PE$<name>, <paths>), 1, 0L);
        """
        .replace("<name>", name)
        .replace("<paths>", String.join(", ", paths)));
  }

  void writeInstanceAccessors(
      Writer out, String className, IndexedField indexed)
      throws IOException {
    writeSegmentHelpers(out, indexed, false);
    if (indexed.addressElement()) {
      writeAddressHelpers(out, className, indexed);
    }

    var field = indexed.element();
    var name = field.name();
    var params = indexed.parameterDeclarations();
    var args = indexed.parameterNames();
    var needsAllocator = analyzer.indexedElementNeedsAllocator(indexed);
    var valueName = unusedParameterName(indexed, "value");
    var allocatorName = unusedParameterName(indexed, "allocator");
    var allocator = needsAllocator
        ? "java.lang.foreign.SegmentAllocator " + allocatorName + ", " : "";

    if (indexed.addressElement() && !field.isMemorySegment()) {
      var foreignClass = foreignMemoryClassName(field);
      out.write("""

            public <type> <name>(<params>) {
              var address = <name>$Address(<args>);
              return address.address() == 0L
                  ? null
                  : <foreignClass>.reinterpret(address);
            }
          """
          .replace("<type>", field.typeName())
          .replace("<name>", name)
          .replace("<params>", params)
          .replace("<args>", args)
          .replace("<foreignClass>", foreignClass));
    } else {
      out.write("""

            public <type> <name>(<params>) {
              return <expression>;
            }
          """
          .replace("<type>", field.typeName())
          .replace("<name>", name)
          .replace("<params>", params)
          .replace("<expression>", getterExpression(
              indexed, "ms", args, false)));
    }

    out.write("""

          public <class> <name>(
              <allocator><params>,
              <type> <valueName>) {
            <body>
            return this;
          }
        """
        .replace("<class>", className)
        .replace("<name>", name)
        .replace("<allocator>", allocator)
        .replace("<params>", params)
        .replace("<type>", field.typeName())
        .replace("<valueName>", valueName)
        .replace("<body>", setterBody(
            indexed, "ms", args, false, needsAllocator)));

    if (indexed.oneDimensional() && indexed.primitive()) {
      writePrimitiveConveniences(out, className, indexed, false);
    }
  }

  void writeStaticAccessors(Writer out, IndexedField indexed)
      throws IOException {
    writeSegmentHelpers(out, indexed, true);

    var field = indexed.element();
    var name = field.name();
    var params = indexed.parameterDeclarations();
    var args = indexed.parameterNames();
    var needsAllocator = analyzer.indexedElementNeedsAllocator(indexed);
    var valueName = unusedParameterName(indexed, "value");
    var allocatorName = unusedParameterName(indexed, "allocator");
    var allocator = needsAllocator
        ? "java.lang.foreign.SegmentAllocator " + allocatorName + ", " : "";

    out.write("""

          public static <type> <name>(
              java.lang.foreign.MemorySegment ms, <params>) {
            return <expression>;
          }

          public static void <name>(
              java.lang.foreign.MemorySegment ms, <allocator><params>,
              <type> <valueName>) {
            <body>
          }
        """
        .replace("<type>", field.typeName())
        .replace("<name>", name)
        .replace("<params>", params)
        .replace("<allocator>", allocator)
        .replace("<valueName>", valueName)
        .replace("<expression>", getterExpression(
            indexed, "ms", args, true))
        .replace("<body>", setterBody(
            indexed, "ms", args, true, needsAllocator)));

    if (indexed.oneDimensional() && indexed.primitive()) {
      writePrimitiveConveniences(out, null, indexed, true);
    } else if (indexed.recordSnapshot()) {
      writeRecordArrayConveniences(out, indexed, needsAllocator);
    }
  }

  private void writeSegmentHelpers(
      Writer out, IndexedField indexed, boolean isStatic)
      throws IOException {
    var name = indexed.element().name();
    var params = indexed.parameterDeclarations();
    var receiverAndParams = isStatic
        ? "java.lang.foreign.MemorySegment ms"
            + indexed.commaPrefixedParameterDeclarations()
        : params;
    var offsetArgument = (leafOffset(indexed) + ",")
        .replace("\n", "\n        ");

    var wholeFieldTemplate = isStatic
        ? """

              public static java.lang.foreign.MemorySegment
                  <name>$MemorySegment(java.lang.foreign.MemorySegment ms) {
                return ms.asSlice(FM$OFFSET$<name>, FM$SIZE$<name>);
              }
            """
        : """

              public java.lang.foreign.MemorySegment <name>$MemorySegment() {
                return ms.asSlice(FM$OFFSET$<name>, FM$SIZE$<name>);
              }
            """;
    out.write(wholeFieldTemplate.replace("<name>", name));

    var elementTemplate = isStatic
        ? """

              public static java.lang.foreign.MemorySegment
                  <name>$MemorySegment(
                  <receiverAndParams>) {
                return ms.asSlice(
                    <offsetArgument>
                    FM$ELEMENT_LAYOUT$<name>.byteSize());
              }
            """
        : """

              public java.lang.foreign.MemorySegment
                  <name>$MemorySegment(<receiverAndParams>) {
                return ms.asSlice(
                    <offsetArgument>
                    FM$ELEMENT_LAYOUT$<name>.byteSize());
              }
            """;
    out.write(elementTemplate
        .replace("<name>", name)
        .replace("<receiverAndParams>", receiverAndParams)
        .replace("<offsetArgument>", offsetArgument));
  }

  private void writeAddressHelpers(Writer out, String className,
      IndexedField indexed) throws IOException {
    var name = indexed.element().name();
    var params = indexed.parameterDeclarations();
    var args = indexed.parameterNames();
    var vhArgs = "ms, " + args;
    var valueName = unusedParameterName(indexed, "value");

    out.write("""

          public java.lang.foreign.MemorySegment <name>$Address(<params>) {
            return (java.lang.foreign.MemorySegment)
                FM$VH$<name>.get(<vhArgs>);
          }

          public <class> <name>$Address(
              <params>, java.lang.foreign.MemorySegment <valueName>) {
            FM$VH$<name>.set(<vhArgs>,
                <valueName> == null
                    ? java.lang.foreign.MemorySegment.NULL : <valueName>);
            return this;
          }
        """
        .replace("<class>", className)
        .replace("<name>", name)
        .replace("<params>", params)
        .replace("<valueName>", valueName)
        .replace("<vhArgs>", vhArgs));
  }

  private String getterExpression(IndexedField indexed,
      String segment, String args, boolean isStatic) {
    var field = indexed.element();
    var name = field.name();
    var vhArgs = segment + ", " + args;

    if (indexed.primitive())
      return "(<type>) FM$VH$<name>.get(<arguments>)"
          .replace("<type>", field.typeName())
          .replace("<name>", name)
          .replace("<arguments>", vhArgs);

    if (field.isMemorySegment()) {
      if (isStatic)
        throw new IllegalStateException(
            "Record snapshots cannot contain MemorySegment arrays");

      return "<name>$Address(<arguments>)"
          .replace("<name>", name)
          .replace("<arguments>", args);
    }

    if (indexed.structuredValueElement()) {
      var foreignClass = foreignMemoryClassName(field);
      var leaf = elementSegmentCall(indexed, segment, args, isStatic);
      var template = field.isRecord() ? """
          <foreignClass>.fromMemorySegment(
                  <leaf>)
          """ : """
          new <foreignClass>(
                  <leaf>)
          """;
      return template
          .replace("<foreignClass>", foreignClass)
          .replace("<leaf>", leaf)
          .strip();
    }

    throw new IllegalStateException("Unsupported indexed getter: " + name);
  }

  private String setterBody(IndexedField indexed, String segment,
      String args, boolean isStatic, boolean withAllocator) {
    var field = indexed.element();
    var name = field.name();
    var vhArgs = segment + ", " + args;
    var valueName = unusedParameterName(indexed, "value");
    var allocatorName = unusedParameterName(indexed, "allocator");

    if (indexed.primitive())
      return "FM$VH$<name>.set(<arguments>, <value>);"
          .replace("<name>", name)
          .replace("<arguments>", vhArgs)
          .replace("<value>", valueName);

    if (field.isMemorySegment())
      return """
          FM$VH$<name>.set(<arguments>,
                  <value> == null
                      ? java.lang.foreign.MemorySegment.NULL : <value>);
          """
          .replace("<name>", name)
          .replace("<arguments>", vhArgs)
          .replace("<value>", valueName)
          .strip();

    var foreignClass = foreignMemoryClassName(field);

    if (indexed.addressElement()) {
      var template = field.isRecord()
          ? """
              FM$VH$<name>.set(<arguments>,
                      <value> == null
                          ? java.lang.foreign.MemorySegment.NULL
                          : <foreignClass>.toMemorySegment(
                              <allocator>, <value>));
              """
          : """
              FM$VH$<name>.set(<arguments>,
                      <value> == null
                          ? java.lang.foreign.MemorySegment.NULL
                          : ((<foreignClass>) <value>).ms);
              """;

      return template
          .replace("<name>", name)
          .replace("<arguments>", vhArgs)
          .replace("<value>", valueName)
          .replace("<foreignClass>", foreignClass)
          .replace("<allocator>", allocatorName)
          .strip();
    }

    var leaf = elementSegmentCall(indexed, segment, args, isStatic);

    if (field.isRecord())
      return """
          <foreignClass>.toMemorySegment(
                  <value>, <leaf><allocatorArgument>);
          """
          .replace("<foreignClass>", foreignClass)
          .replace("<value>", valueName)
          .replace("<leaf>", leaf)
          .replace("<allocatorArgument>",
              withAllocator ? ", " + allocatorName : "")
          .strip();

    return """
        java.lang.foreign.MemorySegment.copy(
                ((<foreignClass>) <value>).ms, 0L,
                <leaf>, 0L,
                FM$ELEMENT_LAYOUT$<name>.byteSize());
        """
        .replace("<foreignClass>", foreignClass)
        .replace("<value>", valueName)
        .replace("<leaf>", leaf)
        .replace("<name>", name)
        .strip();
  }

  private String elementSegmentCall(IndexedField indexed,
      String segment, String args, boolean isStatic) {
    var arguments = isStatic ? segment + ", " + args : args;

    return "<name>$MemorySegment(<arguments>)"
        .replace("<name>", indexed.element().name())
        .replace("<arguments>", arguments);
  }

  private String unusedParameterName(IndexedField indexed, String preferred) {
    var usedNames = indexed.dimensions().stream()
        .map(IndexedField.Dimension::name)
        .collect(java.util.stream.Collectors.toSet());
    var name = preferred;
    while (usedNames.contains(name)) {
      name += "$";
    }
    return name;
  }

  private String leafOffset(IndexedField indexed) {
    var paths = indexed.dimensions().stream()
        .map(d -> "java.lang.foreign.MemoryLayout.PathElement.sequenceElement("
            + d.name() + ")")
        .toList();

    return """
        FM$LAYOUT.byteOffset(
            FM$PE$<name>,
            <paths>)
        """
        .replace("<name>", indexed.element().name())
        .replace("<paths>", String.join(",\n    ", paths))
        .strip();
  }

  private void writePrimitiveConveniences(Writer out,
      String className, IndexedField indexed, boolean isStatic)
      throws IOException {
    var name = indexed.element().name();
    var type = indexed.element().typeName();
    var dimension = indexed.dimensions().getFirst();
    var indexType = dimension.typeName();
    var segmentCall = name + "$MemorySegment(" + (isStatic ? "ms" : "") + ")";
    var bufferType = primitiveBufferType(type);
    var bufferConversion = switch (type) {
      case "boolean", "byte" -> "";
      default -> ".as" + capitalize(type) + "Buffer()";
    };

    var template = isStatic
        ? """

              public static java.nio.<bufferType> <name>$Buffer(
                  java.lang.foreign.MemorySegment ms) {
                return <segmentCall>.asByteBuffer()
                    .order(java.nio.ByteOrder.nativeOrder())<bufferConversion>;
              }

              public static <type>[] <name>$Array(
                  java.lang.foreign.MemorySegment ms) {
                var value = new <type>[(int) FM$DIMENSION$<name>$0];
                for (<indexType> index = 0; index < value.length; index++) {
                  value[(int) index] = <name>(ms, index);
                }
                return value;
              }

              public static <type>[] <name>(
                  java.lang.foreign.MemorySegment ms) {
                return <name>$Array(ms);
              }

              public static void <name>(
                  java.lang.foreign.MemorySegment ms, <type>[] value) {
                java.util.Objects.requireNonNull(value, "value");
                if (value.length != FM$DIMENSION$<name>$0) {
                  throw new IllegalArgumentException(
                      "<name> length must be " + FM$DIMENSION$<name>$0);
                }
                for (<indexType> index = 0; index < value.length; index++) {
                  <name>(ms, index, value[(int) index]);
                }
              }
            """
        : """

              public java.nio.<bufferType> <name>$Buffer() {
                return <segmentCall>.asByteBuffer()
                    .order(java.nio.ByteOrder.nativeOrder())<bufferConversion>;
              }

              public <type>[] <name>$Array() {
                var value = new <type>[(int) FM$DIMENSION$<name>$0];
                for (<indexType> index = 0; index < value.length; index++) {
                  value[(int) index] = <name>(index);
                }
                return value;
              }

              public <class> <name>(<type>[] value) {
                java.util.Objects.requireNonNull(value, "value");
                if (value.length != FM$DIMENSION$<name>$0) {
                  throw new IllegalArgumentException(
                      "<name> length must be " + FM$DIMENSION$<name>$0);
                }
                for (<indexType> index = 0; index < value.length; index++) {
                  <name>(index, value[(int) index]);
                }
                return this;
              }
            """;
    out.write(template
        .replace("<bufferType>", bufferType)
        .replace("<bufferConversion>", bufferConversion)
        .replace("<segmentCall>", segmentCall)
        .replace("<indexType>", indexType)
        .replace("<class>", className == null ? "" : className)
        .replace("<name>", name)
        .replace("<type>", type));
  }

  private void writeRecordArrayConveniences(Writer out,
      IndexedField indexed, boolean needsAllocator) throws IOException {
    var name = indexed.element().name();
    var elementType = indexed.element().typeName();
    var arrayType = indexed.declaredTypeName();
    var allocatorParameter = needsAllocator
        ? "java.lang.foreign.SegmentAllocator allocator, " : "";
    var allocatorArgument = needsAllocator ? "allocator, " : "";

    out.write("""

          public static <arrayType> <name>$Array(
              java.lang.foreign.MemorySegment ms) {
            var value = new <elementType>[(int) FM$DIMENSION$<name>$0];
            for (long index = 0; index < value.length; index++) {
              value[(int) index] = <name>(ms, index);
            }
            return value;
          }

          public static <arrayType> <name>(
              java.lang.foreign.MemorySegment ms) {
            return <name>$Array(ms);
          }

          public static void <name>(java.lang.foreign.MemorySegment ms,
              <allocatorParameter><arrayType> value) {
            java.util.Objects.requireNonNull(value, "value");
            if (value.length != FM$DIMENSION$<name>$0) {
              throw new IllegalArgumentException(
                  "<name> length must be " + FM$DIMENSION$<name>$0);
            }
            for (long index = 0; index < value.length; index++) {
              <name>(ms, <allocatorArgument>index, value[(int) index]);
            }
          }
        """
        .replace("<allocatorParameter>", allocatorParameter)
        .replace("<allocatorArgument>", allocatorArgument)
        .replace("<arrayType>", arrayType)
        .replace("<elementType>", elementType)
        .replace("<name>", name));
  }

  private String foreignMemoryClassName(VariableGenerator field) {
    return ProcessorUtils.foreignMemoryClassName(
        field.typeElement, elements);
  }

  private String primitiveBufferType(String primitive) {
    return switch (primitive) {
      case "boolean", "byte" -> "ByteBuffer";
      case "char" -> "CharBuffer";
      case "short" -> "ShortBuffer";
      case "int" -> "IntBuffer";
      case "long" -> "LongBuffer";
      case "float" -> "FloatBuffer";
      case "double" -> "DoubleBuffer";
      default -> throw new IllegalArgumentException(
          "Unexpected primitive: " + primitive);
    };
  }

  private String capitalize(String name) {
    return Character.toTitleCase(name.charAt(0)) + name.substring(1);
  }
}

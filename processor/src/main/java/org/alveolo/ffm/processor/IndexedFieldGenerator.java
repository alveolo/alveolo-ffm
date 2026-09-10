package org.alveolo.ffm.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.stream.IntStream;

/// Generates the metadata and accessors for one inline indexed field.
///
/// Each writing entry point emits one complete section of the field so the
/// enclosing memory generator can keep all field-specific declarations
/// close together.
final class IndexedFieldGenerator {
  private final ForeignMemoryAnalyzer analyzer;

  IndexedFieldGenerator(ForeignMemoryAnalyzer analyzer) {
    this.analyzer = analyzer;
  }

  void writeMetadata(Writer out, IndexedField indexed) throws IOException {
    var name = indexed.element().name();

    out.write("""

          public static final java.lang.foreign.MemoryLayout.PathElement
              <name>$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
                  .groupElement("<name>");
        """
        .replace("<name>", name));

    for (var i = 0; i < indexed.dimensions().size(); i++) {
      out.write("""

            public static final java.lang.foreign.MemoryLayout.PathElement
                <name>$Sequence<index>PathElement$F =
                    java.lang.foreign.MemoryLayout.PathElement
                        .sequenceElement();
          """
          .replace("<name>", name)
          .replace("<index>", Integer.toString(i)));
    }

    out.write("""

          public static final java.lang.foreign.MemoryLayout
              <name>$MemoryLayout$F =
                  MemoryLayout$F.select(<name>$PathElement$F);

          public static final java.lang.foreign.MemoryLayout
              <name>$ElementMemoryLayout$F =
              <elementLayout>;
        """
        .replace("<name>", name)
        .replace("<elementLayout>", indexed.element().layout()));

    for (var i = 0; i < indexed.dimensions().size(); i++) {
      out.write("""

            public static final long <name>$Sequence<index>Dimension$F =
                <size>L;
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
        .mapToObj(i -> name + "$Sequence" + i + "PathElement$F")
        .toList();

    out.write("""

          public static final java.lang.invoke.VarHandle <name>$VarHandle$F =
              java.lang.invoke.MethodHandles.insertCoordinates(
                  MemoryLayout$F.varHandle(
                      <name>$PathElement$F, <paths>), 1, 0L);
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
    var params = indexed.accessorParameterDeclarations();
    var args = indexed.accessorParameterNames();
    var needsAllocator = analyzer.indexedElementNeedsAllocator(indexed);
    var valueName = "value$f";
    var allocatorName = "allocator$f";
    var allocator = needsAllocator
        ? "java.lang.foreign.SegmentAllocator " + allocatorName + ", " : "";

    out.write("""

          public <type> <name>(<params>) {
            return <expression>;
          }
        """
        .replace("<type>", field.typeName())
        .replace("<name>", name)
        .replace("<params>", params)
        .replace("<expression>", getterExpression(
            indexed, "MemorySegment$F", args, false)));

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
            indexed, "MemorySegment$F", args, false, needsAllocator)));

    if (indexed.oneDimensional() && indexed.primitive()) {
      writePrimitiveConveniences(out, className, indexed, false);
    }
  }

  void writeInheritedFluentSetters(
      Writer out, String className, IndexedField indexed)
      throws IOException {
    var field = indexed.element();
    var name = field.name();
    var params = indexed.accessorParameterDeclarations();
    var args = indexed.accessorParameterNames();
    var needsAllocator = analyzer.indexedElementNeedsAllocator(indexed);
    var allocator = needsAllocator
        ? "java.lang.foreign.SegmentAllocator allocator$f, " : "";
    var allocatorArg = needsAllocator ? "allocator$f, " : "";

    out.write("""

          @Override
          public <class> <name>(
              <allocator><params>,
              <type> value$f) {
            return (<class>) super.<name>(
                <allocatorArg><args>, value$f);
          }
        """
        .replace("<class>", className)
        .replace("<name>", name)
        .replace("<allocator>", allocator)
        .replace("<allocatorArg>", allocatorArg)
        .replace("<params>", params)
        .replace("<args>", args)
        .replace("<type>", field.typeName()));

    if (indexed.addressElement()) {
      var helperParams = indexed.helperParameterDeclarations();
      var helperArgs = indexed.helperParameterNames();
      out.write("""

            @Override
            public <class> <name>AsAddress$F(
                <params>, java.lang.foreign.MemorySegment value) {
              return (<class>) super.<name>AsAddress$F(<args>, value);
            }
          """
          .replace("<class>", className)
          .replace("<name>", name)
          .replace("<params>", helperParams)
          .replace("<args>", helperArgs));
    }

    if (indexed.oneDimensional() && indexed.primitive()) {
      out.write("""

            @Override
            public <class> <name>FromArray$F(<type>[] value) {
              return (<class>) super.<name>FromArray$F(value);
            }
          """
          .replace("<class>", className)
          .replace("<name>", name)
          .replace("<type>", field.typeName()));
    }
  }

  void writeStaticAccessors(Writer out, IndexedField indexed)
      throws IOException {
    writeSegmentHelpers(out, indexed, true);

    var field = indexed.element();
    var name = field.name();
    var params = indexed.accessorParameterDeclarations();
    var args = indexed.accessorParameterNames();
    var needsAllocator = analyzer.indexedElementNeedsAllocator(indexed);
    var valueName = "value";
    var allocatorName = "allocator";
    var allocator = needsAllocator
        ? "java.lang.foreign.SegmentAllocator " + allocatorName + ", " : "";

    out.write("""

          public static <type> <name>(
              java.lang.foreign.MemorySegment memorySegment, <params>) {
            return <expression>;
          }

          public static void <name>(
              java.lang.foreign.MemorySegment memorySegment,
              <allocator><params>,
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
            indexed, "memorySegment", args, true))
        .replace("<body>", setterBody(
            indexed, "memorySegment", args, true, needsAllocator)));

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
    var params = indexed.helperParameterDeclarations();
    var receiverAndParams = isStatic
        ? "java.lang.foreign.MemorySegment memorySegment, " + params
        : params;
    var offsetArgument = (leafOffset(indexed) + ",")
        .replace("\n", "\n        ");

    var wholeFieldTemplate = isStatic
        ? """

              public static java.lang.foreign.MemorySegment
                  <name>AsMemorySegment$F(
                      java.lang.foreign.MemorySegment memorySegment) {
                return memorySegment.asSlice(
                    MemoryLayout$F.byteOffset(<name>$PathElement$F),
                    <name>$MemoryLayout$F.byteSize());
              }
            """
        : """

              public java.lang.foreign.MemorySegment
                  <name>AsMemorySegment$F() {
                return MemorySegment$F.asSlice(
                    MemoryLayout$F.byteOffset(<name>$PathElement$F),
                    <name>$MemoryLayout$F.byteSize());
              }
            """;
    out.write(wholeFieldTemplate.replace("<name>", name));

    var elementTemplate = isStatic
        ? """

              public static java.lang.foreign.MemorySegment
                  <name>AsMemorySegment$F(
                  <receiverAndParams>) {
                return memorySegment.asSlice(
                    <offsetArgument>
                    <name>$ElementMemoryLayout$F.byteSize());
              }
            """
        : """

              public java.lang.foreign.MemorySegment
                  <name>AsMemorySegment$F(<receiverAndParams>) {
                return MemorySegment$F.asSlice(
                    <offsetArgument>
                    <name>$ElementMemoryLayout$F.byteSize());
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
    var params = indexed.helperParameterDeclarations();
    var args = indexed.helperParameterNames();
    var vhArgs = "MemorySegment$F, " + args;
    var valueName = "value";

    out.write("""

          public java.lang.foreign.MemorySegment <name>AsAddress$F(<params>) {
            return (java.lang.foreign.MemorySegment)
                <name>$VarHandle$F.get(<vhArgs>);
          }

          public <class> <name>AsAddress$F(
              <params>, java.lang.foreign.MemorySegment <valueName>) {
            <name>$VarHandle$F.set(<vhArgs>,
                <address>);
            return this;
          }
        """
        .replace("<class>", className)
        .replace("<name>", name)
        .replace("<params>", params)
        .replace("<address>", FieldConversions.nullableAddress(valueName, valueName)
            .replace("\n", "\n        "))
        .replace("<valueName>", valueName)
        .replace("<vhArgs>", vhArgs));
  }

  private String getterExpression(IndexedField indexed,
      String segment, String args, boolean isStatic) {
    var field = indexed.element();
    var name = field.name();
    var vhArgs = segment + ", " + args;

    if (indexed.primitive())
      return "(<type>) <name>$VarHandle$F.get(<arguments>)"
          .replace("<type>", field.typeName())
          .replace("<name>", name)
          .replace("<arguments>", vhArgs);

    if (field.isMemorySegment()) {
      if (isStatic)
        throw new IllegalStateException(
            "Record snapshots cannot contain MemorySegment arrays");

      return "<name>AsAddress$F(<arguments>)"
          .replace("<name>", name)
          .replace("<arguments>", args);
    }

    if (indexed.addressElement())
      return FieldConversions.readAddress(field,
          name + "AsAddress$F(" + args + ")");

    if (indexed.structuredValueElement())
      return FieldConversions.readValue(field,
          elementSegmentCall(indexed, segment, args, isStatic))
          .replace("\n", "\n    ");

    throw new IllegalStateException("Unsupported indexed getter: " + name);
  }

  private String setterBody(IndexedField indexed, String segment,
      String args, boolean isStatic, boolean withAllocator) {
    var field = indexed.element();
    var name = field.name();
    var vhArgs = segment + ", " + args;
    var valueName = isStatic ? "value" : "value$f";
    var allocatorName = isStatic ? "allocator" : "allocator$f";

    if (indexed.primitive())
      return "<name>$VarHandle$F.set(<arguments>, <value>);"
          .replace("<name>", name)
          .replace("<arguments>", vhArgs)
          .replace("<value>", valueName);

    if (indexed.addressElement())
      return name + "$VarHandle$F.set(" + vhArgs + ",\n        "
          + FieldConversions.addressValue(field, valueName, allocatorName)
              .replace("\n", "\n        ") + ");";

    return FieldConversions.writeValue(field, valueName,
        elementSegmentCall(indexed, segment, args, isStatic),
        name + "$ElementMemoryLayout$F.byteSize()",
        withAllocator ? allocatorName : null).replace("\n", "\n    ");
  }

  private String elementSegmentCall(IndexedField indexed,
      String segment, String args, boolean isStatic) {
    var arguments = isStatic ? segment + ", " + args : args;

    return "<name>AsMemorySegment$F(<arguments>)"
        .replace("<name>", indexed.element().name())
        .replace("<arguments>", arguments);
  }

  private String leafOffset(IndexedField indexed) {
    var paths = IntStream.range(0, indexed.dimensions().size())
        .mapToObj(i ->
            "java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index"
                + i + ")")
        .toList();

    return """
        MemoryLayout$F.byteOffset(
            <name>$PathElement$F,
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
    var segmentCall = name + "AsMemorySegment$F("
        + (isStatic ? "memorySegment" : "") + ")";
    var bufferType = primitiveBufferType(type);
    var bufferConversion = switch (type) {
      case "boolean", "byte" -> "";
      default -> ".as" + capitalize(type) + "Buffer()";
    };

    var template = isStatic
        ? """

              public static java.nio.<bufferType> <name>AsBuffer$F(
                  java.lang.foreign.MemorySegment memorySegment) {
                return <segmentCall>.asByteBuffer()
                    .order(java.nio.ByteOrder.nativeOrder())<bufferConversion>;
              }

              public static <type>[] <name>ToArray$F(
                  java.lang.foreign.MemorySegment memorySegment) {
                var result =
                    new <type>[(int) <name>$Sequence0Dimension$F];
                for (<indexType> index = 0;
                    index < result.length; index++) {
                  result[(int) index] =
                      <name>(memorySegment, index);
                }
                return result;
              }

              public static <type>[] <name>(
                  java.lang.foreign.MemorySegment memorySegment) {
                return <name>ToArray$F(memorySegment);
              }

              public static void <name>(
                  java.lang.foreign.MemorySegment memorySegment,
                  <type>[] value) {
                java.util.Objects.requireNonNull(value, "value");
                if (value.length != <name>$Sequence0Dimension$F) {
                  throw new IllegalArgumentException(
                      "<name> length must be "
                          + <name>$Sequence0Dimension$F);
                }
                for (<indexType> index = 0;
                    index < value.length; index++) {
                  <name>(
                      memorySegment, index, value[(int) index]);
                }
              }
            """
        : """

              public java.nio.<bufferType> <name>AsBuffer$F() {
                return <segmentCall>.asByteBuffer()
                    .order(java.nio.ByteOrder.nativeOrder())<bufferConversion>;
              }

              public <type>[] <name>ToArray$F() {
                var result =
                    new <type>[(int) <name>$Sequence0Dimension$F];
                for (<indexType> index = 0;
                    index < result.length; index++) {
                  result[(int) index] = <name>(index);
                }
                return result;
              }

              public <class> <name>FromArray$F(<type>[] value) {
                java.util.Objects.requireNonNull(value, "value");
                if (value.length != <name>$Sequence0Dimension$F) {
                  throw new IllegalArgumentException(
                      "<name> length must be "
                          + <name>$Sequence0Dimension$F);
                }
                for (<indexType> index = 0;
                    index < value.length; index++) {
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

          public static <arrayType> <name>ToArray$F(
              java.lang.foreign.MemorySegment memorySegment) {
            var result =
                new <elementType>[(int) <name>$Sequence0Dimension$F];
            for (long index = 0;
                index < result.length; index++) {
              result[(int) index] =
                  <name>(memorySegment, index);
            }
            return result;
          }

          public static <arrayType> <name>(
              java.lang.foreign.MemorySegment memorySegment) {
            return <name>ToArray$F(memorySegment);
          }

          public static void <name>(
              java.lang.foreign.MemorySegment memorySegment,
              <allocatorParameter><arrayType> value) {
            java.util.Objects.requireNonNull(value, "value");
            if (value.length != <name>$Sequence0Dimension$F) {
              throw new IllegalArgumentException(
                  "<name> length must be "
                      + <name>$Sequence0Dimension$F);
            }
            for (long index = 0;
                index < value.length; index++) {
              <name>(memorySegment, <allocatorArgument>index,
                  value[(int) index]);
            }
          }
        """
        .replace("<allocatorParameter>", allocatorParameter)
        .replace("<allocatorArgument>", allocatorArgument)
        .replace("<arrayType>", arrayType)
        .replace("<elementType>", elementType)
        .replace("<name>", name));
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

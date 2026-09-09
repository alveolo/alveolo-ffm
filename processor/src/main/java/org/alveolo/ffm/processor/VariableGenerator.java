package org.alveolo.ffm.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.In;
import org.alveolo.ffm.Out;

final class VariableGenerator extends TypeGenerator {
  final Element element;
  final String name;
  final boolean hasExplicitSequence;

  VariableGenerator(
      ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes,
      VariableElement element) {
    this(processingEnv, generatedTypes, element.getSimpleName().toString(),
        element.asType(), sequence(element.asType(), element), element);
  }

  VariableGenerator(
      ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes,
      RecordComponentElement element) {
    this(processingEnv, generatedTypes,
        element.getSimpleName().toString(), element.asType(),
        sequence(element.asType(), element), element);
  }

  VariableGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes, String name,
      TypeMirror typeMirror, long sequence, Element element) {
    super(processingEnv, generatedTypes, typeMirror, element, sequence);
    this.element = element;
    this.name = name;

    hasExplicitSequence = hasSequence(typeMirror, element);
  }

  /// Variable name
  String name() {
    return name;
  }

  /// Variable signature such as: `int i`
  String signature() {
    return typeName() + " " + name();
  }

  String bridgeSignature() {
    return bridgeTypeName() + " " + name();
  }

  String argumentLayout() {
    if (!isCallArrayOrBuffer()) return layout();

    return isCallArrayOrBufferByValue()
        ? callArrayOrBufferValueLayout()
        : "java.lang.foreign.ValueLayout.ADDRESS";
  }

  @Override
  boolean unsupported() {
    return VALUE_LAYOUT_NOT_SUPPORTED.equals(argumentLayout());
  }

  @Override
  boolean needsConfinedArena() {
    return isCallArrayOrBuffer() || super.needsConfinedArena();
  }

  /// Source code for passing an argument to a native function
  /// * `argX` for primitive types or directly passed `MemorySegment` or
  ///   `SegmentAllocator`
  /// * `argX.MemorySegment$F` for a struct/union implementation by reference
  /// * `((StructFM) argX).MemorySegment$F` for an interface by reference
  /// * `arena$f.allocateFrom(argX)` for Java `String` to C `char*` conversion
  /// * `argX$CFString$f` for Java `@CFString String` conversion
  /// * `StructFM.toMemorySegment$F(arena$f, argX)` for records conversion
  String invoke() {
    if (isPrimitiveAddress())
      return segmentName();

    if (isCallArrayOrBuffer())
      return segmentName();

    if (isPrimitive() || isMemorySegment() || isSegmentAllocator())
      return name();

    if (isCFString())
      return cfStringName();

    if (isString())
      return nullableInvoke("arena$f.allocateFrom(" + name() + ")");

    var expression = isForeignMemoryImplementation()
        ? name() + ".MemorySegment$F"
        : isRecord()
            ? foreignMemoryClassName()
                + ".toMemorySegment$F(arena$f, " + name() + ")"
            : "((" + foreignMemoryClassName() + ") " + name()
                + ").MemorySegment$F";
    return isAddress() && !isCallState() ? nullableInvoke(expression) : expression;
  }

  private String nullableInvoke(String expression) {
    // A reference conditional passed to invokeExact otherwise has type Object.
    return "(java.lang.foreign.MemorySegment) (" + name
        + " == null ? java.lang.foreign.MemorySegment.NULL : "
        + expression + ")";
  }

  boolean needsLocalAllocation() {
    return isPrimitiveAddress()
        || isCallArrayOrBuffer()
        || isRecord()
        || isString() && !isCFString();
  }

  String plannedPreparation() {
    if (isString() && !isCFString())
      return "var " + bytesName() + " = " + name() + " == null ? null : "
          + name() + ".getBytes(java.nio.charset.StandardCharsets.UTF_8);";

    if (!isCallArrayOrBuffer()) return "";

    if (isArray())
      return """
          <sizeInitializer>
          <sequenceCheck>
          """
          .replace("<sizeInitializer>\n", sizeInitializer())
          .replace("<sequenceCheck>\n", sequenceCheck("length"))
          .stripTrailing();

    return """
        var <position> = <positionExpression>;
        <sizeInitializer>
        <sequenceCheck>
        <readOnlyCheck>
        <directOrderCheck>
        var <direct> = <directExpression>;
        """
        .replace("<position>", positionName())
        .replace("<positionExpression>", nullableArrayOrBuffer()
            ? name + " == null ? 0 : " + name + ".position()"
            : name + ".position()")
        .replace("<directExpression>", nullableArrayOrBuffer()
            ? name + " != null && " + sizeName() + " != 0 && "
                + name + ".isDirect()"
            : name + ".isDirect()")
        .replace("<name>", name)
        .replace("<sizeInitializer>\n", sizeInitializer())
        .replace("<sequenceCheck>\n", sequenceCheck("remaining"))
        .replace("<readOnlyCheck>\n", readOnlyCheck())
        .replace("<directOrderCheck>\n", directOrderCheck())
        .replace("<direct>", directName())
        .stripTrailing();
  }

  String plannedInitializer(String memorySegment) {
    if (isPrimitiveAddress()) {
      var initialize = hasCanonicalScalar()
          ? canonicalSet(segmentName(), "0L", name)
          : segmentName() + ".set(" + valueLayout() + ", 0L, " + name
              + ");";
      return """
          var <segment> = <memorySegment>;
          <initialize>
          """
          .replace("<segment>", segmentName())
          .replace("<memorySegment>", memorySegment)
          .replace("<initialize>", initialize)
          .stripTrailing();
    }

    if (isString() && !isCFString())
      return """
          var <segment> = <name> == null
              ? java.lang.foreign.MemorySegment.NULL : <memorySegment>;
          if (<name> != null) {
            java.lang.foreign.MemorySegment.copy(
                <bytes>, 0, <segment>,
                java.lang.foreign.ValueLayout.JAVA_BYTE, 0, <bytes>.length);
            <segment>.set(
                java.lang.foreign.ValueLayout.JAVA_BYTE, <bytes>.length,
                (byte) 0);
          }
          """
          .replace("<segment>", segmentName())
          .replace("<name>", name)
          .replace("<memorySegment>", memorySegment)
          .replace("<bytes>", bytesName())
          .stripTrailing();

    if (isRecord()) {
      var conversion = foreignMemoryClassName()
          + ".toMemorySegment$F(" + name + ", " + segmentName() + ");";
      return "var " + segmentName() + " = "
          + (isAddress() ? nullableInvoke(memorySegment) : memorySegment)
          + ";\n"
          + (isAddress() ? "if (" + name + " != null) {\n  "
              + conversion + "\n}" : conversion);
    }

    var storage = isArray() ? memorySegment
        : directName() + "\n    ? java.lang.foreign.MemorySegment.ofBuffer("
            + name + ")\n    : " + memorySegment;
    if (nullableArrayOrBuffer()) {
      storage = name + " == null\n    ? java.lang.foreign.MemorySegment.NULL\n"
          + "    : " + storage.replace("\n", "\n    ");
    }
    var copy = !copyIn() ? "" : isValueStructRecordArray() ? recordArrayCopyIn()
        : isArray() ? arrayCopyIn() : bufferCopyIn();
    return """
        var <segment> = <storage>;
        <copyIn>
        """
        .replace("<segment>", segmentName())
        .replace("<storage>", storage)
        .replace("<copyIn>", copy)
        .stripTrailing();
  }

  String plannedInvoke() {
    return isRecord() || isString() && !isCFString()
        ? segmentName() : invoke();
  }

  String allocationByteSize() {
    if (isString() && !isCFString())
      return "(" + name + " == null ? 0L : Math.addExact((long) "
          + bytesName() + ".length, 1L))";

    if (isCallArrayOrBuffer()) {
      var size = arrayOrBufferAllocationSize();
      if (nullableArrayOrBuffer()) {
        return "(" + name + " == null"
            + (isNioBuffer() ? " || " + directName() : "")
            + " ? 0L : " + size + ")";
      }
      return isNioBuffer() ? directName() + " ? 0L : " + size : size;
    }

    var size = allocationLayout() + ".byteSize()";
    return isRecord() && isAddress()
        ? "(" + name + " == null ? 0L : " + size + ")" : size;
  }

  String allocationAlignment() {
    return isString() && !isCFString()
        ? "1L" : allocationLayout() + ".byteAlignment()";
  }

  private String allocationLayout() {
    if (isPrimitiveAddress()) return valueLayout();
    if (isValueStructRecordArray())
      return recordForeignMemoryClassName() + ".MemoryLayout$F";
    if (isCallArrayOrBuffer()) return elementLayout();
    if (isRecord()) return foreignMemoryClassName() + ".MemoryLayout$F";

    throw new IllegalStateException(
        "Variable does not need a local allocation: " + name);
  }

  String cfStringName() {
    return name() + "$CFString$f";
  }

  boolean hasInAnnotation() {
    return element.getAnnotation(In.class) != null
        || typeMirror.getAnnotation(In.class) != null;
  }

  boolean hasOutAnnotation() {
    return element.getAnnotation(Out.class) != null
        || typeMirror.getAnnotation(Out.class) != null;
  }

  boolean hasConflictingTransferAnnotations() {
    return hasInAnnotation() && hasOutAnnotation();
  }

  boolean hasSequenceOnUnsupportedType() {
    return hasExplicitSequence && !isCallArrayOrBuffer();
  }

  boolean hasInvalidSequence() {
    return hasExplicitSequence && sequence <= 0L;
  }

  boolean isCallArrayOrBuffer() {
    return isArrayOrBuffer() || isValueStructRecordArray();
  }

  boolean isCallArrayOrBufferByValue() {
    return isCallArrayOrBuffer() && hasExplicitValuePassMode();
  }

  private boolean nullableArrayOrBuffer() {
    return isCallArrayOrBuffer() && !isCallArrayOrBufferByValue();
  }

  private String callArrayOrBufferValueLayout() {
    if (hasConflictingPassModeAnnotations())
      return VALUE_LAYOUT_NOT_SUPPORTED;

    var itemLayout = isValueStructRecordArray()
        ? recordForeignMemoryClassName() + ".MemoryLayout$F"
        : elementLayout();

    return "java.lang.foreign.MemoryLayout.structLayout("
        + "java.lang.foreign.MemoryLayout.sequenceLayout("
        + sequence + "L, " + itemLayout + "))";
  }

  String arrayOrBufferInitializer() {
    return plannedPreparation() + "\n" + plannedInitializer(
        "arena$f.allocate(\n    " + arrayOrBufferAllocationSize() + ",\n    "
            + allocationAlignment() + ")");
  }

  private String arrayOrBufferAllocationSize() {
    var size = "Math.multiplyExact(" + allocationLayout()
        + ".byteSize(), (long) " + sizeName() + ")";
    return nullableArrayOrBuffer() ? "Math.max(1L, " + size + ")" : size;
  }

  String primitiveAddressInitializer() {
    if (hasCanonicalScalar()) return """
        var <segment> = arena$f.allocate(<layout>);
        <set>
        """
        .replace("<segment>", segmentName())
        .replace("<layout>", valueLayout())
        .replace("<set>", canonicalSet(segmentName(), "0L", name))
        .stripTrailing();

    return """
        var <segment> = arena$f.allocate(<layout>);
        <segment>.set(<layout>, 0L, <name>);
        """
        .replace("<segment>", segmentName())
        .replace("<layout>", valueLayout())
        .replace("<name>", name)
        .stripTrailing();
  }

  String arrayOrBufferCopyOut() {
    if (!copyOut()) return "";

    return isValueStructRecordArray() ? recordArrayCopyOut()
        : isArray() ? arrayCopyOut()
        : bufferCopyOut();
  }

  private boolean copyIn() {
    return !hasOutAnnotation();
  }

  private boolean copyOut() {
    return !isCallArrayOrBufferByValue() && !hasInAnnotation();
  }

  private String sizeInitializer() {
    return "var " + sizeName() + " = "
        + (nullableArrayOrBuffer() ? name + " == null ? 0 : " : "") + name
        + (isArray() ? ".length" : ".remaining()") + ";\n";
  }

  private String sequenceCheck(String sizeWord) {
    if (!hasExplicitSequence) return "";

    return """
        if (<present><size> != <sequence>) {
          throw new IllegalArgumentException(
              "<name> <sizeWord> must be <sequence>");
        }
        """
        .replace("<size>", sizeName())
        .replace("<present>", nullableArrayOrBuffer() ? name + " != null && " : "")
        .replace("<sequence>", Long.toString(sequence))
        .replace("<name>", name)
        .replace("<sizeWord>", sizeWord);
  }

  private String readOnlyCheck() {
    if (!copyOut()) return "";

    return """
        if (<name> != null && <name>.isReadOnly()) {
          throw new IllegalArgumentException(
              "<name> must be writable unless annotated @In");
        }
        """
        .replace("<name>", name);
  }

  private String directOrderCheck() {
    if ("byte".equals(elementTypeName())) return "";

    return """
        if (<present><name>.isDirect()
            && !<name>.order().equals(java.nio.ByteOrder.nativeOrder())) {
          throw new IllegalArgumentException(
              "direct <name> must use native byte order");
        }
        """
        .replace("<name>", name)
        .replace("<present>", nullableArrayOrBuffer() ? name + " != null && " : "");
  }

  private String arrayCopyIn() {
    if ("boolean".equals(elementTypeName()))
      return booleanArrayCopyIn();

    return """
        if (<size> != 0) {
          java.lang.foreign.MemorySegment.copy(
              <name>, 0, <segment>, <layout>, 0, <size>);
        }
        """
        .replace("<name>", name)
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<size>", sizeName())
        .stripTrailing();
  }

  private String booleanArrayCopyIn() {
    return """
        for (var <index> = 0; <index> < <size>; <index>++) {
          <segment>.setAtIndex(
              java.lang.foreign.ValueLayout.JAVA_BOOLEAN, <index>,
              <name>[<index>]);
        }
        """
        .replace("<index>", indexName())
        .replace("<size>", sizeName())
        .replace("<segment>", segmentName())
        .replace("<name>", name)
        .stripTrailing();
  }

  private String recordArrayCopyIn() {
    var withAllocator = new ForeignMemoryAnalyzer(
        processingEnv, generatedTypes).recordConverterNeedsAllocator(
            arrayComponentGenerator().typeElement);

    return """
        for (var <index> = 0; <index> < <size>; <index>++) {
          <foreignClass>.toMemorySegment$F(
              <name>[<index>],
              <segment>.asSlice(
                  (long) <index> * <foreignClass>.MemoryLayout$F.byteSize(),
                  <foreignClass>.MemoryLayout$F)<allocator>);
        }
        """
        .replace("<index>", indexName())
        .replace("<size>", sizeName())
        .replace("<segment>", segmentName())
        .replace("<foreignClass>", recordForeignMemoryClassName())
        .replace("<name>", name)
        .replace("<allocator>", withAllocator ? ", arena$f" : "")
        .stripTrailing();
  }

  private String bufferCopyIn() {
    return """
        if (!<direct>) {
          for (var <index> = 0; <index> < <size>; <index>++) {
            <segment>.setAtIndex(<layout>, <index>,
                <name>.get(<position> + <index>));
          }
        }
        """
        .replace("<direct>", directName())
        .replace("<index>", indexName())
        .replace("<size>", sizeName())
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<name>", name)
        .replace("<position>", positionName())
        .stripTrailing();
  }

  private String arrayCopyOut() {
    if ("boolean".equals(elementTypeName()))
      return booleanArrayCopyOut();

    return """
        if (<size> != 0) {
          java.lang.foreign.MemorySegment.copy(
              <segment>, <layout>, 0, <name>, 0, <size>);
        }
        """
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<name>", name)
        .replace("<size>", sizeName())
        .stripTrailing();
  }

  private String booleanArrayCopyOut() {
    return """
        for (var <index> = 0; <index> < <size>; <index>++) {
          <name>[<index>] = <segment>.getAtIndex(
              java.lang.foreign.ValueLayout.JAVA_BOOLEAN, <index>);
        }
        """
        .replace("<index>", indexName())
        .replace("<size>", sizeName())
        .replace("<name>", name)
        .replace("<segment>", segmentName())
        .stripTrailing();
  }

  private String recordArrayCopyOut() {
    return """
        for (var <index> = 0; <index> < <size>; <index>++) {
          <name>[<index>] = <foreignClass>.fromMemorySegment$F(
              <segment>.asSlice(
                  (long) <index> * <foreignClass>.MemoryLayout$F.byteSize(),
                  <foreignClass>.MemoryLayout$F));
        }
        """
        .replace("<index>", indexName())
        .replace("<size>", sizeName())
        .replace("<name>", name)
        .replace("<foreignClass>", recordForeignMemoryClassName())
        .replace("<segment>", segmentName())
        .stripTrailing();
  }

  private String bufferCopyOut() {
    return """
        if (!<direct>) {
          for (var <index> = 0; <index> < <size>; <index>++) {
            <name>.put(<position> + <index>,
                <segment>.getAtIndex(<layout>, <index>));
          }
        }
        """
        .replace("<direct>", directName())
        .replace("<index>", indexName())
        .replace("<size>", sizeName())
        .replace("<name>", name)
        .replace("<position>", positionName())
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .stripTrailing();
  }

  String segmentName() {
    return name + "$MemorySegment$f";
  }

  private String sizeName() {
    return name + "$size$f";
  }

  private String positionName() {
    return name + "$position$f";
  }

  private String directName() {
    return name + "$direct$f";
  }

  private String indexName() {
    return name + "$index$f";
  }

  private String bytesName() {
    return name + "$bytes$f";
  }

  String recordForeignMemoryClassName() {
    return ProcessorUtils.foreignMemoryClassName(
        arrayComponentGenerator().typeElement, elements);
  }
}

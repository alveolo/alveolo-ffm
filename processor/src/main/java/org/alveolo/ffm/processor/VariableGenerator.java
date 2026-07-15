package org.alveolo.ffm.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.CountedBy;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Out;

final class VariableGenerator extends TypeGenerator {
  final Element element;
  final String name;
  final boolean hasExplicitSequence;
  final String countedBy;

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

    var countedByAnnotation = element.getAnnotation(CountedBy.class);
    countedBy = countedByAnnotation == null
        ? null : countedByAnnotation.value();
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
      return "arena$f.allocateFrom(" + name() + ")";

    if (isForeignMemoryImplementation())
      return name() + ".MemorySegment$F";

    if (isRecord())
      return foreignMemoryClassName()
          + ".toMemorySegment$F(arena$f, " + name() + ")";

    return (typeElement.getKind() == ElementKind.INTERFACE)
        ? "((" + foreignMemoryClassName() + ") " + name()
            + ").MemorySegment$F"
        : name() + ".MemorySegment$F";
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

  boolean hasCountedBy() {
    return countedBy != null;
  }

  String countedByName() {
    return countedBy;
  }

  boolean hasConflictingSizeAnnotations() {
    return hasExplicitSequence && hasCountedBy();
  }

  boolean isCountType() {
    if (isPrimitiveAddress()) return false;

    return switch (typeMirror.getKind()) {
      case BYTE, SHORT, INT, LONG -> true;
      default -> false;
    };
  }

  boolean isCallArrayOrBuffer() {
    return isArrayOrBuffer() || isValueStructRecordArray();
  }

  boolean isCallArrayOrBufferByValue() {
    return isCallArrayOrBuffer() && hasExplicitValuePassMode();
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
    return isValueStructRecordArray() ? recordArrayInitializer()
        : isArray() ? arrayInitializer()
        : bufferInitializer();
  }

  String primitiveAddressInitializer() {
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

  private String arrayInitializer() {
    return """
        <sizeInitializer>
        <sequenceCheck>
        var <segment> = arena$f.allocate(<layout>, <size>);
        <copyIn>
        """
        .replace("<sizeInitializer>\n", sizeInitializer(
            name + ".length", "length"))
        .replace("<sequenceCheck>\n", sequenceCheck("length"))
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<size>", sizeName())
        .replace("<copyIn>", copyIn() ? arrayCopyIn() : "")
        .stripTrailing();
  }

  private String bufferInitializer() {
    return """
        var <position> = <name>.position();
        <sizeInitializer>
        <sequenceCheck>
        <readOnlyCheck>
        <directOrderCheck>
        var <direct> = <directExpression>;
        var <segment> = <direct>
            ? java.lang.foreign.MemorySegment.ofBuffer(<name>).asSlice(
                0L, Math.multiplyExact(<layout>.byteSize(), (long) <size>))
            : arena$f.allocate(<layout>, <size>);
        <copyIn>
        """
        .replace("<position>", positionName())
        .replace("<name>", name)
        .replace("<sizeInitializer>\n", sizeInitializer(
            name + ".remaining()", "remaining"))
        .replace("<sequenceCheck>\n", sequenceCheck("remaining"))
        .replace("<readOnlyCheck>\n", readOnlyCheck())
        .replace("<directOrderCheck>\n", directOrderCheck())
        .replace("<direct>", directName())
        .replace("<directExpression>", directExpression())
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<size>", sizeName())
        .replace("<copyIn>", copyIn() ? bufferCopyIn() : "")
        .stripTrailing();
  }

  private String recordArrayInitializer() {
    return """
        <sizeInitializer>
        <sequenceCheck>
        var <segment> = arena$f.allocate(
            <foreignClass>.MemoryLayout$F, <size>);
        <copyIn>
        """
        .replace("<sizeInitializer>\n", sizeInitializer(
            name + ".length", "length"))
        .replace("<sequenceCheck>\n", sequenceCheck("length"))
        .replace("<segment>", segmentName())
        .replace("<foreignClass>", recordForeignMemoryClassName())
        .replace("<size>", sizeName())
        .replace("<copyIn>", copyIn() ? recordArrayCopyIn() : "")
        .stripTrailing();
  }

  private String sizeInitializer(String availableExpression,
      String availableWord) {
    if (!hasCountedBy())
      return "var " + sizeName() + " = " + availableExpression + ";\n";

    return """
        var <available> = <availableExpression>;
        var <count> = (long) <countParameter>;
        if (<count> < 0L || <count> > <available>) {
          throw new IllegalArgumentException(
              "<name> count parameter '<countParameter>' must be between 0 and "
                  + <available> + " (<availableWord>): " + <count>);
        }
        var <size> = (int) <count>;
        """
        .replace("<available>", availableName())
        .replace("<availableExpression>", availableExpression)
        .replace("<count>", countName())
        .replace("<countParameter>", countedBy)
        .replace("<name>", name)
        .replace("<availableWord>", availableWord)
        .replace("<size>", sizeName());
  }

  private String sequenceCheck(String sizeWord) {
    if (!hasExplicitSequence) return "";

    return """
        if (<size> != <sequence>) {
          throw new IllegalArgumentException(
              "<name> <sizeWord> must be <sequence>");
        }
        """
        .replace("<size>", sizeName())
        .replace("<sequence>", Long.toString(sequence))
        .replace("<name>", name)
        .replace("<sizeWord>", sizeWord);
  }

  private String directExpression() {
    return name + ".isDirect()";
  }

  private String readOnlyCheck() {
    if (!copyOut()) return "";

    return """
        if (<name>.isReadOnly()) {
          throw new IllegalArgumentException(
              "<name> must be writable unless annotated @In");
        }
        """
        .replace("<name>", name);
  }

  private String directOrderCheck() {
    if ("byte".equals(elementTypeName())) return "";

    return """
        if (<name>.isDirect()
            && !<name>.order().equals(java.nio.ByteOrder.nativeOrder())) {
          throw new IllegalArgumentException(
              "direct <name> must use native byte order");
        }
        """
        .replace("<name>", name);
  }

  private String arrayCopyIn() {
    if ("boolean".equals(elementTypeName()))
      return booleanArrayCopyIn();

    return """
        java.lang.foreign.MemorySegment.copy(
            <name>, 0, <segment>, <layout>, 0, <size>);
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
    return """
        for (var <index> = 0; <index> < <size>; <index>++) {
          <segment>.asSlice(
              (long) <index> * <foreignClass>.MemoryLayout$F.byteSize(),
              <foreignClass>.MemoryLayout$F).copyFrom(
                  <foreignClass>.toMemorySegment$F(
                      arena$f, <name>[<index>]));
        }
        """
        .replace("<index>", indexName())
        .replace("<size>", sizeName())
        .replace("<segment>", segmentName())
        .replace("<foreignClass>", recordForeignMemoryClassName())
        .replace("<name>", name)
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
        java.lang.foreign.MemorySegment.copy(
            <segment>, <layout>, 0, <name>, 0, <size>);
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

  private String segmentName() {
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

  private String availableName() {
    return name + "$available$f";
  }

  private String countName() {
    return name + "$count$f";
  }

  private String recordForeignMemoryClassName() {
    return ProcessorUtils.foreignMemoryClassName(
        arrayComponentGenerator().typeElement, elements);
  }
}

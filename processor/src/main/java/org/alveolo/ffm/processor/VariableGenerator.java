package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemoryClassName;

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
      VariableElement element) {
    this(processingEnv, element.getSimpleName().toString(), element.asType(),
        sequence(element.asType(), element), element);
  }

  VariableGenerator(
      ProcessingEnvironment processingEnv,
      RecordComponentElement element) {
    this(processingEnv, element.getSimpleName().toString(), element.asType(),
        sequence(element.asType(), element), element);
  }

  VariableGenerator(ProcessingEnvironment processingEnv, String name,
      TypeMirror typeMirror, long sequence, Element element) {
    super(processingEnv, typeMirror, sequence);
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

  String argumentLayout() {
    return isCallArrayOrBuffer()
        ? "java.lang.foreign.ValueLayout.ADDRESS"
        : layout();
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
  /// * `argX.ms` for struct/union implementation passed by reference
  /// * `((StructFM) argX).ms` for struct/union interface passed by reference
  /// * `ff$arena.allocateFrom(argX)` for Java `String` to C `char*` conversion
  /// * `ff$cfString$argX` for Java `@CFString String` conversion
  /// * `StructFM.toMemorySegment(ff$arena, argX)` for records conversion
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
      return "ff$arena.allocateFrom(" + name() + ")";

    if (isRecord())
      return foreignMemoryClassName(typeElement, elements)
          + ".toMemorySegment(ff$arena, " + name() + ")";

    return (typeElement.getKind() == ElementKind.INTERFACE)
        ? "((" + foreignMemoryClassName(typeElement, elements) + ")" + name()
            + ").ms"
        : name() + ".ms";
  }

  String cfStringName() {
    return "ff$cfString$" + name();
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

  String arrayOrBufferInitializer() {
    return isValueStructRecordArray() ? recordArrayInitializer()
        : isArray() ? arrayInitializer()
        : bufferInitializer();
  }

  String primitiveAddressInitializer() {
    return """
        var <segment> = ff$arena.allocate(<layout>);
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
    return !hasInAnnotation();
  }

  private String arrayInitializer() {
    return """
        <sizeInitializer>
        <sequenceCheck>
        var <segment> = ff$arena.allocate(<layout>, <size>);
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
            : ff$arena.allocate(<layout>, <size>);
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
        var <segment> = ff$arena.allocate(
            <foreignClass>.FM$LAYOUT, <size>);
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
          throw new IllegalArgumentException("<name> <sizeWord> must be <sequence>");
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
    if (hasInAnnotation()) return "";

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
              (long) <index> * <foreignClass>.FM$LAYOUT.byteSize(),
              <foreignClass>.FM$LAYOUT).copyFrom(
                  <foreignClass>.toMemorySegment(
                      ff$arena, <name>[<index>]));
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
          <name>[<index>] = <foreignClass>.fromMemorySegment(
              <segment>.asSlice(
                  (long) <index> * <foreignClass>.FM$LAYOUT.byteSize(),
                  <foreignClass>.FM$LAYOUT));
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
    return "ff$ms$" + name;
  }

  private String sizeName() {
    return "ff$size$" + name;
  }

  private String positionName() {
    return "ff$position$" + name;
  }

  private String directName() {
    return "ff$direct$" + name;
  }

  private String indexName() {
    return "ff$i$" + name;
  }

  private String availableName() {
    return "ff$available$" + name;
  }

  private String countName() {
    return "ff$count$" + name;
  }

  private String recordForeignMemoryClassName() {
    return foreignMemoryClassName(arrayComponentGenerator().typeElement,
        elements);
  }
}

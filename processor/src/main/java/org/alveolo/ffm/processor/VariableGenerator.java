package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemoryClassName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;

import org.alveolo.ffm.In;
import org.alveolo.ffm.Out;

class VariableGenerator extends TypeGenerator {
  final Element element;
  final String name;
  final boolean hasExplicitSequence;

  VariableGenerator(
      ProcessingEnvironment processingEnv,
      VariableElement element) {
    super(processingEnv, element.asType(), sequence(element.asType(), element));
    this.element = element;
    name = element.getSimpleName().toString();
    hasExplicitSequence = hasSequence(element.asType(), element);
  }

  VariableGenerator(
      ProcessingEnvironment processingEnv,
      RecordComponentElement element) {
    super(processingEnv, element.asType(), sequence(element.asType(), element));
    this.element = element;
    name = element.getSimpleName().toString();
    hasExplicitSequence = hasSequence(element.asType(), element);
  }

  /// Variable name
  String name() {
    return name;
  }

  /// Variable signature such as: `int i`
  String signature() {
    return typeName() + " " + name();
  }

  @Override
  String layout() {
    if (isArrayOrBuffer()) return "ValueLayout.ADDRESS";

    return super.layout();
  }

  @Override
  boolean needsConfinedArena() {
    return isArrayOrBuffer() || super.needsConfinedArena();
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
    if (isArrayOrBuffer())
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

  boolean isArrayOrBuffer() {
    return elementLayout() != null;
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

  String arrayOrBufferInitializer() {
    return isArray()
        ? arrayInitializer()
        : bufferInitializer();
  }

  String arrayOrBufferCopyOut() {
    if (!copyOut()) return "";

    return isArray()
        ? arrayCopyOut()
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
        var <size> = <name>.length;
        <sequenceCheck>var <segment> = ff$arena.allocate(<layout>, <size>);
        <copyIn>
        """
        .replace("<size>", sizeName())
        .replace("<name>", name)
        .replace("<sequenceCheck>", sequenceCheck("length"))
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<copyIn>", copyIn() ? arrayCopyIn() : "")
        .stripTrailing();
  }

  private String bufferInitializer() {
    return """
        var <position> = <name>.position();
        var <size> = <name>.remaining();
        <sequenceCheck>var <direct> = <directExpression>;
        var <segment> = <direct>
            ? MemorySegment.ofBuffer(<name>)
            : ff$arena.allocate(<layout>, <size>);
        <copyIn>
        """
        .replace("<position>", positionName())
        .replace("<name>", name)
        .replace("<size>", sizeName())
        .replace("<sequenceCheck>", sequenceCheck("remaining"))
        .replace("<direct>", directName())
        .replace("<directExpression>", directExpression())
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<copyIn>", copyIn() ? bufferCopyIn() : "")
        .stripTrailing();
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

  private String arrayCopyIn() {
    return """
        MemorySegment.copy(<name>, 0, <segment>, <layout>, 0, <size>);
        """
        .replace("<name>", name)
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<size>", sizeName())
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
    return """
        MemorySegment.copy(<segment>, <layout>, 0, <name>, 0, <size>);
        """
        .replace("<segment>", segmentName())
        .replace("<layout>", elementLayout())
        .replace("<name>", name)
        .replace("<size>", sizeName())
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

  private boolean isArray() {
    return typeMirror.getKind() == TypeKind.ARRAY;
  }

  private String elementLayout() {
    if (typeMirror.getKind() == TypeKind.ARRAY)
      return switch (((ArrayType) typeMirror).getComponentType().getKind()) {
        case BYTE -> "ValueLayout.JAVA_BYTE";
        case CHAR -> "ValueLayout.JAVA_CHAR";
        case SHORT -> "ValueLayout.JAVA_SHORT";
        case INT -> "ValueLayout.JAVA_INT";
        case LONG -> "ValueLayout.JAVA_LONG";
        case FLOAT -> "ValueLayout.JAVA_FLOAT";
        case DOUBLE -> "ValueLayout.JAVA_DOUBLE";
        default -> null;
      };

    return switch (typeName()) {
      case "java.nio.ByteBuffer" -> "ValueLayout.JAVA_BYTE";
      case "java.nio.CharBuffer" -> "ValueLayout.JAVA_CHAR";
      case "java.nio.ShortBuffer" -> "ValueLayout.JAVA_SHORT";
      case "java.nio.IntBuffer" -> "ValueLayout.JAVA_INT";
      case "java.nio.LongBuffer" -> "ValueLayout.JAVA_LONG";
      case "java.nio.FloatBuffer" -> "ValueLayout.JAVA_FLOAT";
      case "java.nio.DoubleBuffer" -> "ValueLayout.JAVA_DOUBLE";
      default -> null;
    };
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
}

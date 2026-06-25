package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.ForeignUnion;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.macos.CFString;

class TypeGenerator {
  public static String VALUE_LAYOUT_NOT_SUPPORTED = "((ValueLayout) null)";

  public static final String MEMORY_SEGMENT =
      MemorySegment.class.getCanonicalName();

  public static final String SEGMENT_ALLOCATOR =
      SegmentAllocator.class.getCanonicalName();

  public static final String STRING =
      String.class.getCanonicalName();

  final ProcessingEnvironment processingEnv;
  final Elements elements;
  final TypeMirror typeMirror;
  final TypeElement typeElement;
  final long sequence;

  TypeGenerator(ProcessingEnvironment processingEnv,
      TypeMirror typeMirror) {
    this(processingEnv, typeMirror, sequence(typeMirror));
  }

  TypeGenerator(ProcessingEnvironment processingEnv,
      TypeMirror typeMirror, long sequence) {
    this.processingEnv = processingEnv;
    elements = processingEnv.getElementUtils();
    this.typeMirror = typeMirror;
    typeElement = (TypeElement) processingEnv
        .getTypeUtils().asElement(typeMirror);
    this.sequence = sequence;
  }

  /// Java type name
  String typeName() {
    if (typeMirror instanceof DeclaredType dt)
      return elements.getBinaryName((TypeElement) dt.asElement()).toString();

    return typeMirror.toString();
  }

  /// MemoryLayout type such as:
  /// * `ValueLayout.JAVA_INT` for primitive types
  /// * `Nested.FM$LAYOUT` for nested structs/unions
  /// * `ValueLayout.ADDRESS` for reference types
  /// * `MemoryLayout.sequenceLayout(5, ValueLayout.JAVA_INT)` for
  ///   primitive arrays
  // TODO support nested struct/union and reference arrays
  String layout() {
    if (typeElement != null) {
      if (isForeignMemory()) {
        if (hasConflictingPassModeAnnotations()) return VALUE_LAYOUT_NOT_SUPPORTED;
        return isValue()
            ? foreignClassName(typeElement) + ".FM$LAYOUT"
            : "ValueLayout.ADDRESS";
      }

      if (typeElement.getKind() == ElementKind.CLASS) {
        if (hasConflictingPassModeAnnotations()) return VALUE_LAYOUT_NOT_SUPPORTED;

        if (!hasTypeUseAddress() && !hasTypeUseValue()
            && !hasTypeAddress() && !hasTypeValue())
          return primitiveLayout();

        if (isValue()) return foreignClassName(typeElement) + ".FM$LAYOUT";
        if (isAddress()) return "ValueLayout.ADDRESS";
      }
    }

    return primitiveLayout();
  }

  private String primitiveLayout() {
    return switch (typeName()) {
      case "boolean" -> "ValueLayout.JAVA_BOOLEAN";
      case "byte" -> "ValueLayout.JAVA_BYTE";
      case "char" -> "ValueLayout.JAVA_CHAR";
      case "short" -> "ValueLayout.JAVA_SHORT";
      case "int" -> "ValueLayout.JAVA_INT";
      case "long" -> "ValueLayout.JAVA_LONG";
      case "float" -> "ValueLayout.JAVA_FLOAT";
      case "double" -> "ValueLayout.JAVA_DOUBLE";

      case "java.lang.String" -> "ValueLayout.ADDRESS";
      case "java.lang.foreign.MemorySegment" -> "ValueLayout.ADDRESS";

      case "byte[]", "java.nio.ByteBuffer" -> "MemoryLayout.sequenceLayout("
          + sequence + ", ValueLayout.JAVA_BYTE)";
      case "char[]", "java.nio.CharBuffer" -> "MemoryLayout.sequenceLayout("
          + sequence + ", ValueLayout.JAVA_CHAR)";
      case "short[]", "java.nio.ShortBuffer" -> "MemoryLayout.sequenceLayout("
          + sequence + ", ValueLayout.JAVA_SHORT)";
      case "int[]", "java.nio.IntBuffer" -> "MemoryLayout.sequenceLayout("
          + sequence + ", ValueLayout.JAVA_INT)";
      case "long[]", "java.nio.LongBuffer" -> "MemoryLayout.sequenceLayout("
          + sequence + ", ValueLayout.JAVA_LONG)";
      case "float[]", "java.nio.FloatBuffer" -> "MemoryLayout.sequenceLayout("
          + sequence + ", ValueLayout.JAVA_FLOAT)";
      case "double[]", "java.nio.DoubleBuffer" -> "MemoryLayout.sequenceLayout("
          + sequence + ", ValueLayout.JAVA_DOUBLE)";

      // TODO more custom structures

      default -> VALUE_LAYOUT_NOT_SUPPORTED;
    };
  }

  private static long sequence(TypeMirror typeMirror) {
    if (typeMirror.getKind() == TypeKind.ARRAY)
      return sequence(((ArrayType) typeMirror).getComponentType());

    for (var annotationMirror : typeMirror.getAnnotationMirrors()) {
      if (annotationMirror.getAnnotationType().toString()
          .equals(Sequence.class.getCanonicalName()))
        return annotationMirror.getElementValues().entrySet().stream()
            .filter(e -> e.getKey().getSimpleName().toString().equals("value"))
            .map(e -> e.getValue())
            .map(v -> v.getValue())
            .map(Long.class::cast)
            .findFirst().orElseThrow();
    }

    return 1;
    // throw new IllegalArgumentException("Missing @Sequence annotation");
  }

  // /// Checks if using the type in a call needs allocator.
  // ///
  // /// @return
  // /// - `true` for non-primitive types passed by value
  // /// - `false` for primitive types or complex types passes as address
  // boolean needsAllocator() {
  // return !isPrimitive() && (isRecord() || isString() || isValue());
  // }

  boolean isPrimitive() {
    return typeMirror.getKind().isPrimitive();
  }

  boolean isRecord() {
    return typeElement != null && typeElement.getKind() == ElementKind.RECORD;
  }

  boolean isString() {
    return typeName().equals(STRING);
  }

  boolean isCFString() {
    return typeMirror.getAnnotation(CFString.class) != null;
  }

  boolean isOwnedCFString() {
    var cfString = typeMirror.getAnnotation(CFString.class);
    return cfString != null && cfString.owned();
  }

  boolean needsConfinedArena() {
    return isRecord() || (isString() && !isCFString());
  }

  boolean isMemorySegment() {
    return typeName().equals(MEMORY_SEGMENT);
  }

  boolean isSegmentAllocator() {
    return typeName().equals(SEGMENT_ALLOCATOR);
  }

  boolean isAddress() {
    if (isPrimitive()) return false;

    if (hasTypeUseAddress()) return true;
    if (hasTypeUseValue()) return false;

    if (hasTypeAddress()) return true;
    if (hasTypeValue()) return false;

    if (isForeignMemory())
      return typeElement.getKind() == ElementKind.INTERFACE;

    // default
    return false;
  }

  boolean isValue() {
    if (isPrimitive()) return true;

    if (hasTypeUseAddress()) return false;
    if (hasTypeUseValue()) return true;

    if (hasTypeAddress()) return false;
    if (hasTypeValue()) return true;

    if (isForeignMemory())
      return typeElement.getKind() == ElementKind.RECORD;

    // default
    return true;
  }

  boolean isForeignMemory() {
    return typeElement != null
        && (typeElement.getAnnotation(ForeignStruct.class) != null
            || typeElement.getAnnotation(ForeignUnion.class) != null);
  }

  boolean hasConflictingPassModeAnnotations() {
    return (hasTypeUseAddress() && hasTypeUseValue())
        || (!hasTypeUseAddress() && !hasTypeUseValue()
            && hasTypeAddress() && hasTypeValue());
  }

  private boolean hasTypeUseAddress() {
    return typeMirror.getAnnotation(Address.class) != null;
  }

  private boolean hasTypeUseValue() {
    return typeMirror.getAnnotation(Value.class) != null;
  }

  private boolean hasTypeAddress() {
    return typeElement != null && typeElement.getAnnotation(Address.class) != null;
  }

  private boolean hasTypeValue() {
    return typeElement != null && typeElement.getAnnotation(Value.class) != null;
  }
}

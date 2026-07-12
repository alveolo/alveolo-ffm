package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemoryClassName;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.macos.CFString;

sealed class TypeGenerator permits VariableGenerator {
  /// Dedicated #layout() value representing invalid type.
  ///
  /// Intentionally spoiling runtime to throw on use but keeping the generated
  /// code compilable.
  protected static final String VALUE_LAYOUT_NOT_SUPPORTED =
      "((ValueLayout) null)";

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

  TypeGenerator(ProcessingEnvironment processingEnv, TypeMirror typeMirror) {
    this(processingEnv, typeMirror, sequence(typeMirror));
  }

  TypeGenerator(ProcessingEnvironment processingEnv, TypeMirror typeMirror,
      long sequence) {
    this.processingEnv = processingEnv;
    elements = processingEnv.getElementUtils();
    this.typeMirror = typeMirror;
    typeElement = (TypeElement) processingEnv
        .getTypeUtils().asElement(typeMirror);
    this.sequence = sequence;
  }

  /// Java type name
  String typeName() {
    if (typeMirror.getKind().isPrimitive())
      return switch (typeMirror.getKind()) {
        case BOOLEAN -> "boolean";
        case BYTE -> "byte";
        case CHAR -> "char";
        case SHORT -> "short";
        case INT -> "int";
        case LONG -> "long";
        case FLOAT -> "float";
        case DOUBLE -> "double";
        default -> throw new IllegalStateException(
            "Unexpected primitive type: " + typeMirror);
      };

    if (typeMirror instanceof DeclaredType dt)
      return elements.getBinaryName((TypeElement) dt.asElement()).toString();

    return typeMirror.toString();
  }

  /// MemoryLayout type such as:
  /// * `ValueLayout.JAVA_INT` for primitive types
  /// * `Nested.FM$LAYOUT` for nested structs/unions
  /// * `ValueLayout.ADDRESS` for reference types
  /// * `MemoryLayout.sequenceLayout(5L, ValueLayout.JAVA_INT)` for primitive
  ///   arrays and NIO buffers
  // TODO support nested struct/union and reference arrays
  String layout() {
    if (hasConflictingPassModeAnnotations())
      return VALUE_LAYOUT_NOT_SUPPORTED;

    if (isPrimitiveAddress())
      return "ValueLayout.ADDRESS";

    if (isForeignMemory())
      return isValue()
          ? foreignMemoryClassName(typeElement, elements) + ".FM$LAYOUT"
          : "ValueLayout.ADDRESS";

    return directLayout();
  }

  private String directLayout() {
    if (typeMirror.getKind().isPrimitive())
      return valueLayout();

    var elementLayout = elementLayout();
    if (elementLayout != null)
      return "MemoryLayout.sequenceLayout("
          + sequence + "L, " + elementLayout + ")";

    if (isString() || isMemorySegment())
      return "ValueLayout.ADDRESS";

    // TODO more custom structures
    return VALUE_LAYOUT_NOT_SUPPORTED;
  }

  boolean unsupported() {
    return VALUE_LAYOUT_NOT_SUPPORTED.equals(layout());
  }

  boolean isArrayOrBuffer() {
    return elementLayout() != null;
  }

  boolean isArray() {
    return typeMirror.getKind() == TypeKind.ARRAY;
  }

  boolean isNioBuffer() {
    return bufferElementKind() != null;
  }

  TypeMirror elementType() {
    if (typeMirror.getKind() == TypeKind.ARRAY) {
      var componentType = ((ArrayType) typeMirror).getComponentType();
      return componentType.getKind().isPrimitive() ? componentType : null;
    }

    var kind = bufferElementKind();
    return kind == null ? null
        : processingEnv.getTypeUtils().getPrimitiveType(kind);
  }

  String elementTypeName() {
    var elementType = elementType();
    return elementType == null ? null : elementType.toString();
  }

  String elementLayout() {
    var kind = elementKind();
    return kind == null ? null : switch (kind) {
      case BOOLEAN -> "ValueLayout.JAVA_BOOLEAN";
      case BYTE -> "ValueLayout.JAVA_BYTE";
      case CHAR -> "ValueLayout.JAVA_CHAR";
      case SHORT -> "ValueLayout.JAVA_SHORT";
      case INT -> "ValueLayout.JAVA_INT";
      case LONG -> "ValueLayout.JAVA_LONG";
      case FLOAT -> "ValueLayout.JAVA_FLOAT";
      case DOUBLE -> "ValueLayout.JAVA_DOUBLE";
      default -> null;
    };
  }

  TypeMirror arrayComponentType() {
    return typeMirror.getKind() == TypeKind.ARRAY
        ? ((ArrayType) typeMirror).getComponentType()
        : null;
  }

  TypeGenerator arrayComponentGenerator() {
    var component = arrayComponentType();
    return component == null ? null
        : new TypeGenerator(processingEnv, component);
  }

  boolean isValueStructRecordArray() {
    var component = arrayComponentGenerator();
    return component != null
        && component.isRecord()
        && component.typeElement.getAnnotation(Struct.class) != null
        && component.isValue()
        && !component.hasConflictingPassModeAnnotations();
  }

  private TypeKind elementKind() {
    if (typeMirror.getKind() == TypeKind.ARRAY) {
      var componentKind = ((ArrayType) typeMirror)
          .getComponentType().getKind();
      return componentKind.isPrimitive() ? componentKind : null;
    }

    return bufferElementKind();
  }

  private TypeKind bufferElementKind() {
    return switch (typeName()) {
      case "java.nio.ByteBuffer" -> TypeKind.BYTE;
      case "java.nio.CharBuffer" -> TypeKind.CHAR;
      case "java.nio.ShortBuffer" -> TypeKind.SHORT;
      case "java.nio.IntBuffer" -> TypeKind.INT;
      case "java.nio.LongBuffer" -> TypeKind.LONG;
      case "java.nio.FloatBuffer" -> TypeKind.FLOAT;
      case "java.nio.DoubleBuffer" -> TypeKind.DOUBLE;
      default -> null;
    };
  }

  String valueLayout() {
    return switch (typeMirror.getKind()) {
      case BOOLEAN -> "ValueLayout.JAVA_BOOLEAN";
      case BYTE -> "ValueLayout.JAVA_BYTE";
      case CHAR -> "ValueLayout.JAVA_CHAR";
      case SHORT -> "ValueLayout.JAVA_SHORT";
      case INT -> "ValueLayout.JAVA_INT";
      case LONG -> "ValueLayout.JAVA_LONG";
      case FLOAT -> "ValueLayout.JAVA_FLOAT";
      case DOUBLE -> "ValueLayout.JAVA_DOUBLE";
      default -> throw new IllegalArgumentException(
          "Unexpected primitive type: " + typeMirror);
    };
  }

  static long sequence(TypeMirror typeMirror) {
    return sequence(typeMirror, null);
  }

  static long sequence(TypeMirror typeMirror, Element element) {
    var value = sequenceValue(typeMirror, element);
    return value == null ? 1 : value;
  }

  static boolean hasSequence(TypeMirror typeMirror, Element element) {
    return sequenceValue(typeMirror, element) != null;
  }

  private static Long sequenceValue(TypeMirror typeMirror, Element element) {
    if (element != null) {
      var elementValue = sequenceValue(element.getAnnotationMirrors());
      if (elementValue != null) return elementValue;
    }

    var typeValue = sequenceValue(typeMirror.getAnnotationMirrors());
    if (typeValue != null) return typeValue;

    if (typeMirror.getKind() == TypeKind.ARRAY)
      return sequenceValue(((ArrayType) typeMirror).getComponentType(), null);

    return null;
  }

  private static Long sequenceValue(
      Iterable<? extends AnnotationMirror> annotationMirrors) {
    for (var annotationMirror : annotationMirrors) {
      if (annotationMirror.getAnnotationType().toString()
          .equals(Sequence.class.getCanonicalName()))
        return annotationMirror.getElementValues().entrySet().stream()
            .filter(e -> e.getKey().getSimpleName().toString().equals("value"))
            .map(e -> e.getValue())
            .map(v -> v.getValue())
            .map(Long.class::cast)
            .findFirst().orElse(1L);
    }

    return null;
  }

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
    return isPrimitiveAddress() || isRecord() || (isString() && !isCFString());
  }

  boolean isMemorySegment() {
    return typeName().equals(MEMORY_SEGMENT);
  }

  boolean isSegmentAllocator() {
    return typeName().equals(SEGMENT_ALLOCATOR);
  }

  boolean isAddress() {
    if (isPrimitive()) return isPrimitiveAddress();

    if (hasTypeUseAddress()) return true;
    if (hasTypeUseValue()) return false;

    if (hasTypeAddress()) return true;
    if (hasTypeValue()) return false;

    if (isForeignMemory())
      return typeElement.getKind() == ElementKind.INTERFACE;

    // default
    return false;
  }

  boolean hasAddressAnnotation() {
    return hasTypeUseAddress() || hasTypeAddress();
  }

  boolean isPrimitiveAddress() {
    return isPrimitive() && hasTypeUseAddress();
  }

  boolean isValue() {
    if (isPrimitive()) return !isPrimitiveAddress();

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
        && (typeElement.getAnnotation(Struct.class) != null
            || typeElement.getAnnotation(Union.class) != null);
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
    return typeElement != null
        && typeElement.getAnnotation(Address.class) != null;
  }

  private boolean hasTypeValue() {
    return typeElement != null
        && typeElement.getAnnotation(Value.class) != null;
  }
}

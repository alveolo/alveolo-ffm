package org.alveolo.ffm.processor;

import java.lang.annotation.Annotation;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.util.ArrayList;
import java.util.Arrays;

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
import javax.lang.model.util.Types;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.CallState;
import org.alveolo.ffm.NativeType;
import org.alveolo.ffm.SLong;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.SizeT;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.ULong;
import org.alveolo.ffm.Union;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.WCharT;
import org.alveolo.ffm.macos.CFString;

sealed class TypeGenerator permits VariableGenerator {
  enum CanonicalScalar {
    SLONG(SLong.class, TypeKind.LONG),
    ULONG(ULong.class, TypeKind.LONG),
    SIZE_T(SizeT.class, TypeKind.LONG),
    WCHAR_T(WCharT.class, TypeKind.INT);

    final Class<? extends Annotation> annotation;
    final TypeKind javaKind;
    final String nativeType;
    final String accessorSuffix;

    CanonicalScalar(Class<? extends Annotation> annotation, TypeKind javaKind) {
      this.annotation = annotation;
      this.javaKind = javaKind;
      nativeType = NativeType.class.getCanonicalName() + "." + name();
      accessorSuffix = annotation.getSimpleName();
    }
  }

  /// Dedicated #layout() value representing invalid type.
  ///
  /// Intentionally spoiling runtime to throw on use but keeping the generated
  /// code compilable.
  protected static final String VALUE_LAYOUT_NOT_SUPPORTED =
      "((java.lang.foreign.ValueLayout) null)";

  public static final String MEMORY_SEGMENT =
      MemorySegment.class.getCanonicalName();

  public static final String SEGMENT_ALLOCATOR =
      SegmentAllocator.class.getCanonicalName();

  public static final String LINKER_OPTION =
      Linker.Option.class.getCanonicalName();

  public static final String STRING =
      String.class.getCanonicalName();

  final ProcessingEnvironment processingEnv;
  final Elements elements;
  final Types types;
  final GeneratedTypeRegistry generatedTypes;
  final Element useSite;
  final TypeMirror typeMirror;
  final TypeElement typeElement;
  final GeneratedTypeRegistry.Wrapper generatedWrapper;
  final BufferType bufferType;
  final long sequence;

  final boolean ambiguousCanonicalScalars;
  final CanonicalScalar canonicalScalar;
  final boolean foreignMemoryImplementation;
  final boolean foreignMemory;
  final boolean typeUseAddress;
  final boolean typeUseValue;
  final boolean typeAddress;
  final boolean typeValue;

  private final boolean callState;
  private final CFString cfString;

  TypeGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes, TypeMirror typeMirror,
      Element useSite) {
    this(processingEnv, generatedTypes, typeMirror, useSite,
        sequence(typeMirror));
  }

  TypeGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes, TypeMirror typeMirror,
      Element useSite, long sequence) {
    this.processingEnv = processingEnv;
    elements = processingEnv.getElementUtils();
    types = processingEnv.getTypeUtils();
    this.generatedTypes = generatedTypes;
    this.useSite = useSite;
    this.typeMirror = typeMirror;
    typeElement = (TypeElement) types.asElement(typeMirror);
    generatedWrapper = generatedTypes.find(typeMirror, useSite);
    bufferType = BufferType.forType(typeName());
    this.sequence = sequence;

    ambiguousCanonicalScalars = Arrays.stream(CanonicalScalar.values())
        .filter(scalar -> hasTypeUseAnnotation(typeMirror, scalar.annotation))
        .count() > 1;
    canonicalScalar = Arrays.stream(CanonicalScalar.values())
        .filter(scalar -> hasTypeUseAnnotation(typeMirror, scalar.annotation))
        .findFirst().orElse(null);
    typeUseAddress = hasTypeUseAnnotation(typeMirror, Address.class);
    typeUseValue = hasTypeUseAnnotation(typeMirror, Value.class);
    cfString = typeMirror.getAnnotation(CFString.class);

    foreignMemoryImplementation = generatedWrapper != null
        || hasWrapperMemorySegment(typeElement);
    foreignMemory = foreignMemoryImplementation
        || isForeignMemorySpecification(typeElement);

    var annotationSource = resolveTypeAnnotationSource();
    typeAddress = annotationSource != null
        && annotationSource.getAnnotation(Address.class) != null;
    typeValue = annotationSource != null
        && annotationSource.getAnnotation(Value.class) != null;
    callState = hasCallStateLinkerOption(typeElement)
        || annotationSource != null
            && annotationSource.getAnnotation(CallState.class) != null;
  }

  /// Java type name
  String typeName() {
    if (generatedWrapper != null)
      return generatedWrapper.className();

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

  /// Source type used in an intermediate generated specification. Type-use
  /// annotations must survive so the processor consuming that specification sees
  /// the same native pass mode.
  String bridgeTypeName() {
    if (generatedWrapper == null)
      return typeMirror.toString();

    var annotations = new ArrayList<String>();

    for (var annotation : typeMirror.getAnnotationMirrors()) {
      annotations.add(annotation.toString());
    }

    if (!typeUseAddress && !typeUseValue) {
      if (typeAddress) {
        annotations.add("@" + Address.class.getCanonicalName());
      } else if (typeValue) {
        annotations.add("@" + Value.class.getCanonicalName());
      }
    }

    if (annotations.isEmpty())
      return generatedWrapper.className();

    var annotationSource = String.join(" ", annotations);
    var className = generatedWrapper.className();
    var separator = className.lastIndexOf('.');

    return separator < 0 ? annotationSource + " " + className
        : className.substring(0, separator + 1) + annotationSource
            + " " + className.substring(separator + 1);
  }

  /// MemoryLayout type such as:
  /// * `ValueLayout.JAVA_INT` for primitive types
  /// * `Nested.MemoryLayout$F` for nested structs/unions
  /// * `ValueLayout.ADDRESS` for reference types
  /// * `MemoryLayout.sequenceLayout(5L, ValueLayout.JAVA_INT)` for primitive
  ///   arrays and NIO buffers
  String layout() {
    if (hasConflictingPassModeAnnotations() || canonicalScalarError() != null)
      return VALUE_LAYOUT_NOT_SUPPORTED;

    if (isPrimitiveAddress())
      return "java.lang.foreign.ValueLayout.ADDRESS";

    if (!foreignMemory)
      return directLayout();

    return isValue()
        ? foreignMemoryClassName() + ".MemoryLayout$F"
        : "java.lang.foreign.ValueLayout.ADDRESS";
  }

  private String directLayout() {
    if (typeMirror.getKind().isPrimitive())
      return valueLayout();

    var elementLayout = elementLayout();
    if (elementLayout != null)
      return "java.lang.foreign.MemoryLayout.sequenceLayout("
          + sequence + "L, " + elementLayout + ")";

    if (isString() || isMemorySegment())
      return "java.lang.foreign.ValueLayout.ADDRESS";

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
    return bufferType != null;
  }

  TypeMirror elementType() {
    if (typeMirror.getKind() == TypeKind.ARRAY) {
      var componentType = ((ArrayType) typeMirror).getComponentType();
      return componentType.getKind().isPrimitive() ? componentType : null;
    }

    var kind = bufferElementKind();
    return kind == null ? null : types.getPrimitiveType(kind);
  }

  String elementTypeName() {
    var elementType = elementType();
    return elementType == null ? null : elementType.toString();
  }

  String elementLayout() {
    return primitiveLayout(elementKind());
  }

  TypeMirror arrayComponentType() {
    return typeMirror.getKind() != TypeKind.ARRAY ? null
        : ((ArrayType) typeMirror).getComponentType();
  }

  TypeGenerator arrayComponentGenerator() {
    var component = arrayComponentType();
    return component == null ? null
        : new TypeGenerator(processingEnv, generatedTypes, component, useSite);
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
      var componentKind = ((ArrayType) typeMirror).getComponentType().getKind();
      return componentKind.isPrimitive() ? componentKind : null;
    }

    return bufferElementKind();
  }

  private TypeKind bufferElementKind() {
    return bufferType == null ? null : TypeKind.valueOf(bufferType.name());
  }

  String valueLayout() {
    if (canonicalScalar != null)
      return canonicalScalar.nativeType + ".layout";

    var layout = primitiveLayout(typeMirror.getKind());
    if (layout == null)
      throw new IllegalArgumentException(
          "Unexpected primitive type: " + typeMirror);

    return layout;
  }

  private static String primitiveLayout(TypeKind kind) {
    return switch (kind) {
      case BOOLEAN -> "java.lang.foreign.ValueLayout.JAVA_BOOLEAN";
      case BYTE -> "java.lang.foreign.ValueLayout.JAVA_BYTE";
      case CHAR -> "java.lang.foreign.ValueLayout.JAVA_CHAR";
      case SHORT -> "java.lang.foreign.ValueLayout.JAVA_SHORT";
      case INT -> "java.lang.foreign.ValueLayout.JAVA_INT";
      case LONG -> "java.lang.foreign.ValueLayout.JAVA_LONG";
      case FLOAT -> "java.lang.foreign.ValueLayout.JAVA_FLOAT";
      case DOUBLE -> "java.lang.foreign.ValueLayout.JAVA_DOUBLE";
      case null, default -> null;
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
    return cfString != null;
  }

  boolean isOwnedCFString() {
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

    if (typeUseAddress) return true;
    if (typeUseValue) return false;

    if (typeAddress) return true;
    if (typeValue) return false;

    if (foreignMemoryImplementation) return true;

    if (foreignMemory)
      return typeElement.getKind() == ElementKind.INTERFACE;

    // default
    return false;
  }

  boolean isPrimitiveAddress() {
    return isPrimitive() && typeUseAddress;
  }

  boolean isValue() {
    return !isAddress();
  }

  boolean isCallState() {
    return callState;
  }

  String foreignMemoryClassName() {
    return foreignMemoryImplementation ? typeName()
        : ProcessorUtils.foreignMemoryClassName(typeElement, elements);
  }

  boolean hasConflictingPassModeAnnotations() {
    return (typeUseAddress && typeUseValue)
        || (!typeUseAddress && !typeUseValue && typeAddress && typeValue);
  }

  boolean hasCanonicalScalar() {
    return canonicalScalar != null;
  }

  boolean needsDowncallAdaptation() {
    return isPrimitive() && !isPrimitiveAddress()
        && canonicalScalar != null && canonicalScalar.nativeType != null;
  }

  String canonicalRuntimeType() {
    return canonicalScalar == null ? "null" : canonicalScalar.nativeType;
  }

  String canonicalGet(String segment, String offset) {
    if (canonicalScalar == null)
      throw new IllegalStateException(
          "Type has no canonical scalar: " + typeMirror);

    return "org.alveolo.ffm.NativeType.get" + canonicalScalar.accessorSuffix
        + "(" + segment + ", " + offset + ")";
  }

  String canonicalSet(String segment, String offset, String value) {
    if (canonicalScalar == null)
      throw new IllegalStateException(
          "Type has no canonical scalar: " + typeMirror);

    return "org.alveolo.ffm.NativeType.set" + canonicalScalar.accessorSuffix
        + "(" + segment + ", " + offset + ", " + value + ");";
  }

  String canonicalScalarError() {
    if (canonicalScalar == null) return null;

    if (ambiguousCanonicalScalars)
      return "Only one of @SLong, @ULong, @SizeT, and @WCharT "
          + "may be used on a type";

    if (!isPrimitive())
      return "@" + canonicalScalar.annotation.getSimpleName()
          + " is only supported on scalar values and @Address scalar pointees";

    if (typeMirror.getKind() != canonicalScalar.javaKind)
      return "@" + canonicalScalar.annotation.getSimpleName()
          + " requires Java " + canonicalScalar.javaKind.name().toLowerCase();

    return null;
  }

  private static boolean hasTypeUseAnnotation(
      TypeMirror type, Class<? extends Annotation> annotation) {
    String annotationName = annotation.getCanonicalName();
    for (var am : type.getAnnotationMirrors()) {
      if (am.getAnnotationType().toString().equals(annotationName))
        return true;
    }

    return type.getKind() == TypeKind.ARRAY
        && hasTypeUseAnnotation(
            ((ArrayType) type).getComponentType(), annotation);
  }

  private TypeElement resolveTypeAnnotationSource() {
    if (generatedWrapper != null)
      return generatedWrapper.specification();

    if (!foreignMemoryImplementation)
      return typeElement;

    for (var iface : typeElement.getInterfaces()) {
      if (types.asElement(iface) instanceof TypeElement spec
          && isForeignMemorySpecification(spec))
        return spec;
    }

    return typeElement;
  }

  private static boolean isForeignMemorySpecification(TypeElement type) {
    return type != null
        && (type.getAnnotation(Struct.class) != null
            || type.getAnnotation(Union.class) != null
            || type.getAnnotation(CallState.class) != null);
  }

  private boolean hasWrapperMemorySegment(TypeElement type) {
    return type != null && type.getKind() == ElementKind.CLASS
        && elements.getAllMembers(type).stream()
            .anyMatch(field -> field.getKind() == ElementKind.FIELD
                && field.getSimpleName().contentEquals("MemorySegment$F")
                && field.asType().toString().equals(MEMORY_SEGMENT));
  }

  private boolean hasCallStateLinkerOption(TypeElement type) {
    return type != null && type.getKind() == ElementKind.CLASS
        && type.getEnclosedElements().stream()
            .anyMatch(field -> field.getKind() == ElementKind.FIELD
                && field.getSimpleName().contentEquals("LinkerOption$F")
                && field.asType().toString().equals(LINKER_OPTION));
  }
}

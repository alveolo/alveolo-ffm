package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignValue;
import org.alveolo.ffm.Sequence;

class TypeGenerator {
  public static String VALUE_LAYOUT_NOT_SUPPORTED = "((ValueLayout) null)";

  final ProcessingEnvironment processingEnv;
  final TypeMirror typeMirror;
  final long sequence;

  TypeGenerator(ProcessingEnvironment processingEnv,
      TypeMirror typeMirror) {
    this.processingEnv = processingEnv;
    this.typeMirror = typeMirror;
    sequence = sequence(typeMirror);
  }

  TypeGenerator(ProcessingEnvironment processingEnv,
      TypeMirror typeMirror, long sequence) {
    this.processingEnv = processingEnv;
    this.typeMirror = typeMirror;
    this.sequence = sequence;
  }

  String typeName() {
    if (typeMirror instanceof DeclaredType dt)
      return processingEnv.getElementUtils()
          .getBinaryName((TypeElement) dt.asElement()).toString();

    return typeMirror.toString();
  }

  String layout() {
    var element = (TypeElement) processingEnv
        .getTypeUtils().asElement(typeMirror);

    if (element != null) {
      if (element.getKind() == ElementKind.RECORD
          && element.getAnnotation(ForeignValue.class) != null)
        return foreignClassName(element) + ".FM$LAYOUT";

      if (element.getKind() == ElementKind.CLASS) {
        boolean hasValue = hasAnnotation(ForeignValue.class);
        boolean hasAddress = hasAnnotation(Address.class);

        // TODO report correct error
        if (hasValue && hasAddress) return VALUE_LAYOUT_NOT_SUPPORTED;

        if (hasValue) return foreignClassName(element) + ".FM$LAYOUT";
        if (hasAddress) return "ValueLayout.ADDRESS";

        hasValue = element.getAnnotation(ForeignValue.class) != null;
        hasAddress = element.getAnnotation(Address.class) != null;

        // TODO report correct error
        if (hasValue && hasAddress) return VALUE_LAYOUT_NOT_SUPPORTED;

        if (hasValue) return foreignClassName(element) + ".FM$LAYOUT";
        if (hasAddress) return "ValueLayout.ADDRESS";
      }
    }

    return switch (typeName()) {
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

      // TODO nesting structures
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

  boolean hasAnnotation(Class<? extends Annotation> annotation) {
    for (var annotationMirror : typeMirror.getAnnotationMirrors()) {
      if (annotationMirror.getAnnotationType().toString()
          .equals(annotation.getCanonicalName()))
        return true;
    }

    return false;
  }

  boolean needsAllocator() {
    var element = processingEnv.getTypeUtils().asElement(typeMirror);
    if (element == null) return false;

    var kind = element.getKind();
    if (kind == ElementKind.RECORD)
      return element.getAnnotation(ForeignValue.class) != null;

    return typeName().equals("java.lang.String");
  }
}

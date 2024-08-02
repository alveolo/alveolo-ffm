package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignValue;

class VariableGenerator extends TypeGenerator {
  final Element element;
  final String name;

  VariableGenerator(
      ProcessingEnvironment processingEnv,
      VariableElement element) {
    super(processingEnv, element.asType());
    this.element = element;
    name = element.getSimpleName().toString();
  }

  VariableGenerator(
      ProcessingEnvironment processingEnv,
      RecordComponentElement element) {
    super(processingEnv, element.asType());
    this.element = element;
    name = element.getSimpleName().toString();
  }

  VariableGenerator(
      ProcessingEnvironment processingEnv,
      TypeMirror typeMirror, long sequence,
      String name, PackageElement element) {
    super(processingEnv, typeMirror, sequence);
    this.element = element;
    this.name = name;
    name = element.getSimpleName().toString();
  }

  String name() {
    return name;
  }

  String signature() {
    return typeName() + " " + name();
  }

  String invoke() {
    if (hasAnnotation(Address.class)) return name() + ".ms";

    if (typeName().equals("java.lang.String"))
      return "ff$arena.allocateUtf8String(" + name() + ")";

    if (!hasAnnotation(ForeignValue.class)) {
      var type = processingEnv.getTypeUtils().asElement(typeMirror);

      if (type != null && type.getKind() == ElementKind.CLASS
          && type.getAnnotation(ForeignValue.class) == null)
        return name() + ".ms";
    }

    if (needsAllocator()) {
      var type = (TypeElement) processingEnv
          .getTypeUtils().asElement(typeMirror);

      return foreignClassName(type)
          + ".toMemorySegment(ff$arena, " + name() + ")";
    }

    return name();
  }
}

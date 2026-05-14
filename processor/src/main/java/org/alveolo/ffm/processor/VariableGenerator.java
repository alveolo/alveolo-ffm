package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.ForeignUnion;
import org.alveolo.ffm.Value;

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

  /// Variable name
  String name() {
    return name;
  }

  /// Variable signature such as: `int i`
  String signature() {
    return typeName() + " " + name();
  }

  /// Source code for passing an argument to a native function
  /// * `argX` for primitive types
  /// * `argX.ms` for struct/union implementation passed by reference
  /// * `((StructFM) argX).ms` for struct/union interface passed by reference
  /// * `ff$arena.allocateFrom(argX)` for Java `String` to C `char*` conversion
  /// * `StructFM.toMemorySegment(ff$arena, argX)` for records conversion
  String invoke() {
    if (hasAnnotation(Address.class)) {
      // Check if the underlying type is a
      // @ForeignStruct/@ForeignUnion interface
      var type = processingEnv.getTypeUtils().asElement(typeMirror);
      if (type instanceof TypeElement typeEl
          && typeEl.getKind() == ElementKind.INTERFACE
          && (typeEl.getAnnotation(ForeignStruct.class) != null
              || typeEl.getAnnotation(ForeignUnion.class) != null))
        return "((" + ProcessorUtils.foreignClassName(typeEl) + ")"
            + name() + ").ms";
      return name() + ".ms";
    }

    if (typeName().equals("java.lang.String"))
      return "ff$arena.allocateFrom(" + name() + ")";

    if (!hasAnnotation(Value.class)) {
      var type = processingEnv.getTypeUtils().asElement(typeMirror);

      if (type != null && type.getKind() == ElementKind.CLASS
          && type.getAnnotation(Value.class) == null)
        return name() + ".ms";

      // Check for @ForeignStruct/@ForeignUnion interface (non-@Address)
      if (type instanceof TypeElement typeEl
          && typeEl.getKind() == ElementKind.INTERFACE
          && (typeEl.getAnnotation(ForeignStruct.class) != null
              || typeEl.getAnnotation(ForeignUnion.class) != null))
        return "((" + ProcessorUtils.foreignClassName(typeEl) + ")"
            + name() + ").ms";
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

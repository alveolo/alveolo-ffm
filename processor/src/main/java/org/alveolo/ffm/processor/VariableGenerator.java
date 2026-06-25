package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.VariableElement;

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
  /// * `argX` for primitive types or directly passed `MemorySegment` or
  ///   `SegmentAllocator`
  /// * `argX.ms` for struct/union implementation passed by reference
  /// * `((StructFM) argX).ms` for struct/union interface passed by reference
  /// * `ff$arena.allocateFrom(argX)` for Java `String` to C `char*` conversion
  /// * `ff$cfString$argX` for Java `@CFString String` conversion
  /// * `StructFM.toMemorySegment(ff$arena, argX)` for records conversion
  String invoke() {
    if (isPrimitive() || isMemorySegment() || isSegmentAllocator())
      return name();

    if (isCFString())
      return cfStringName();

    if (isString())
      return "ff$arena.allocateFrom(" + name() + ")";

    if (isRecord())
      return foreignClassName(typeElement)
          + ".toMemorySegment(ff$arena, " + name() + ")";

    return (typeElement.getKind() == ElementKind.INTERFACE)
        ? "((" + foreignClassName(typeElement) + ")" + name() + ").ms"
        : name() + ".ms";
  }

  String cfStringName() {
    return "ff$cfString$" + name();
  }
}

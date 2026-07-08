package org.alveolo.ffm.processor;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/// Generates the padded array of layout expressions for a struct/union.
class MemoryLayoutGenerator {
  final ProcessingEnvironment processingEnv;
  final List<LayoutField> fields;

  /// Lightweight descriptor for a layout field.
  record LayoutField(
      String name, String layout, boolean unsupported,
      String typeName, Element errorElement
  ) {}

  MemoryLayoutGenerator(ProcessingEnvironment processingEnv,
      List<LayoutField> fields) {
    this.processingEnv = processingEnv;
    this.fields = fields;
  }

  String layout() {
    var buf = new StringBuilder();

    for (var field : fields) {
      if (field.unsupported()) {
        processingEnv.getMessager().printError(
            "Type is not supported: " + field.typeName(), field.errorElement());
      }

      buf.append("        ").append(field.layout())
          .append(".withName(\"").append(field.name()).append("\"),\n");
    }

    return buf.toString();
  }
}

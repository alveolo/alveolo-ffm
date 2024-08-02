package org.alveolo.ffm.processor;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;

class MemoryLayoutGenerator {
  final ProcessingEnvironment processingEnv;
  final List<VariableGenerator> variableGenerators;

  MemoryLayoutGenerator(ProcessingEnvironment processingEnv,
      List<VariableGenerator> variableGenerators) {
    this.processingEnv = processingEnv;
    this.variableGenerators = variableGenerators;
  }

  String layout() {
    var buf = new StringBuilder();

    for (var gen : variableGenerators) {
      var layout = gen.layout();

      if (layout == VariableGenerator.VALUE_LAYOUT_NOT_SUPPORTED) {
        processingEnv.getMessager().printError(
            "Type is not supported: " + gen.typeName(), gen.element);
      }

      buf.append("        ").append(layout)
          .append(".withName(\"<field>\"),\n".replace("<field>", gen.name()));
    }

    return buf.toString();
  }
}

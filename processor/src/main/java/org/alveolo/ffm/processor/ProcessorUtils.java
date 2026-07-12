package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.isIdentifier;
import static javax.lang.model.SourceVersion.isKeyword;

import java.lang.annotation.Annotation;

import javax.lang.model.element.NestingKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;

public class ProcessorUtils {
  private ProcessorUtils() {}

  static <T extends Annotation> void validateSimpleClassName(
      TypeElement element, T annotation, String name) throws ProcessorError {
    if (name.isEmpty()) return;

    if (name.indexOf('.') >= 0 || !isIdentifier(name) || isKeyword(name))
      throw new ProcessorError(element, "@"
          + annotation.annotationType().getSimpleName()
          + " name must be a simple Java class name, not: " + name);
  }

  static <T extends Annotation> void validateTopLevelType(
      TypeElement element, T annotation) throws ProcessorError {
    if (element.getNestingKind() == NestingKind.TOP_LEVEL) return;

    throw new ProcessorError(element,
        "Nested @" + annotation.annotationType().getSimpleName()
            + " types are not yet supported");
  }

  static String foreignMemoryClassName(TypeElement element, Elements elements) {
    String simpleName = foreignMemorySimpleClassName(element);
    return qualifyName(packageName(element, elements), simpleName);
  }

  static String foreignMemorySimpleClassName(TypeElement element) {
    var struct = element.getAnnotation(Struct.class);
    if (struct != null)
      return generatedSimpleClassName(element, struct.name(), "FM");

    var union = element.getAnnotation(Union.class);
    if (union != null)
      return generatedSimpleClassName(element, union.name(), "FM");

    return generatedSimpleClassName(element, "", "FM");
  }

  static String foreignInterfaceClassName(
      TypeElement element, Elements elements) {
    String simpleName = foreignInterfaceSimpleClassName(element);
    return qualifyName(packageName(element, elements), simpleName);
  }

  static String foreignInterfaceSimpleClassName(TypeElement element) {
    var foreignInterface = element.getAnnotation(ForeignInterface.class);
    String override = foreignInterface == null ? "" : foreignInterface.name();

    return generatedSimpleClassName(element, override, "FFM");
  }

  static String dispatchTableClassName(TypeElement element, Elements elements) {
    String simpleName = dispatchTableSimpleClassName(element);
    return qualifyName(packageName(element, elements), simpleName);
  }

  static String dispatchTableSimpleClassName(TypeElement element) {
    var dispatchTable = element.getAnnotation(DispatchTable.class);
    var override = dispatchTable == null ? "" : dispatchTable.name();

    return generatedSimpleClassName(element, override, "FD");
  }

  static String packageName(TypeElement element, Elements elements) {
    return elements.getPackageOf(element).getQualifiedName().toString();
  }

  static String sourceMethodSignature(ExecutableElement method) {
    return "public " + sourceReturnType(method) + " "
        + method.getSimpleName()
        + method.getParameters().stream()
            .map(ProcessorUtils::sourceParameter)
            .collect(joining(",\n      ", "(\n      ", ")"));
  }

  static String sourceReturnType(ExecutableElement method) {
    return method.getReturnType().toString();
  }

  static String sourceParameter(VariableElement parameter) {
    return parameter.asType() + " " + parameter.getSimpleName();
  }

  static String qualifyName(String packageName, String className) {
    return packageName.isEmpty() ? className : packageName + "." + className;
  }

  private static String generatedSimpleClassName(
      TypeElement element, String override, String suffix) {
    return !override.isEmpty() ? override : element.getSimpleName() + suffix;
  }
}

package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.isIdentifier;
import static javax.lang.model.SourceVersion.isKeyword;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.NestingKind;
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

  static <T extends Annotation> void validateGeneratedClassName(
      TypeElement element, T annotation, String name) throws ProcessorError {
    if (name.indexOf('$') < 0) return;

    throw new ProcessorError(element,
        "Generated @" + annotation.annotationType().getSimpleName()
            + " class name must not contain '$': " + name);
  }

  static void validateUserIdentifiers(TypeElement element)
      throws ProcessorError {
    if (isAlveoloGeneratedSpecification(element)) return;

    validateUserIdentifier(element, element.getSimpleName().toString(),
        "type");

    for (var component : element.getRecordComponents()) {
      validateUserIdentifier(component, component.getSimpleName().toString(),
          "record component");
    }

    for (var enclosed : element.getEnclosedElements()) {
      if (!(enclosed instanceof ExecutableElement method)
          || method.getKind() != ElementKind.METHOD) {
        continue;
      }

      validateUserIdentifier(method, method.getSimpleName().toString(),
          "method");
      for (var parameter : method.getParameters()) {
        validateUserIdentifier(parameter,
            parameter.getSimpleName().toString(), "parameter");
      }
    }
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

  static String vtableSpecificationSimpleClassName(TypeElement element) {
    if (hasSpecSuffix(element))
      return foreignMemorySimpleClassName(element) + "VtblSpec";

    return element.getSimpleName() + "Vtbl";
  }

  static String vtableImplementationSimpleClassName(TypeElement element) {
    if (hasSpecSuffix(element))
      return foreignMemorySimpleClassName(element) + "Vtbl";

    return element.getSimpleName() + "VtblFD";
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
    if (!override.isEmpty()) return override;

    var sourceName = element.getSimpleName().toString();
    return hasSpecSuffix(element)
        ? sourceName.substring(0, sourceName.length() - "Spec".length())
        : sourceName + suffix;
  }

  private static boolean hasSpecSuffix(TypeElement element) {
    var name = element.getSimpleName().toString();
    return element.getKind() == ElementKind.INTERFACE
        && name.length() > "Spec".length()
        && name.endsWith("Spec");
  }

  private static void validateUserIdentifier(
      Element element, String name, String kind)
      throws ProcessorError {
    if (!name.endsWith("$F") && !name.endsWith("$f")) return;

    throw new ProcessorError(element,
        "User " + kind + " names ending in '$F' or '$f' are reserved for "
            + "generated identifiers: " + name);
  }

  private static boolean isAlveoloGeneratedSpecification(
      TypeElement element) {
    for (var annotation : element.getAnnotationMirrors()) {
      if (!annotation.getAnnotationType().toString()
          .equals("javax.annotation.processing.Generated")) {
        continue;
      }

      for (var entry : annotation.getElementValues().entrySet()) {
        if (!entry.getKey().getSimpleName().contentEquals("value")) continue;

        if (entry.getValue().getValue() instanceof List<?> values
            && values.stream()
                .filter(AnnotationValue.class::isInstance)
                .map(AnnotationValue.class::cast)
                .map(AnnotationValue::getValue)
                .map(Object::toString)
                .anyMatch(value -> value.startsWith(
                    "org.alveolo.ffm.processor."))) {
          return true;
        }
      }
    }

    return false;
  }
}

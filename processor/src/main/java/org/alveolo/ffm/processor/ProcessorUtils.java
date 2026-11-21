package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.isIdentifier;
import static javax.lang.model.SourceVersion.isKeyword;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import org.alveolo.ffm.CallState;
import org.alveolo.ffm.Callback;
import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;

final class ProcessorUtils {
  private static final Set<String> GENERATORS = Set.of(
      CallState.class.getCanonicalName(),
      Callback.class.getCanonicalName(),
      DispatchTable.class.getCanonicalName(),
      ForeignInterface.class.getCanonicalName(),
      Struct.class.getCanonicalName(),
      Union.class.getCanonicalName());

  private ProcessorUtils() {}

  /// Validation errors point to their offending element; unexpected failures
  /// retain the stack trace and point to the type being processed.
  static void reportError(Messager messager, Throwable error, Element type) {
    if (error instanceof ProcessorError validation) {
      messager.printError(validation.getMessage(), validation.element);
    } else if (error instanceof FilerException) {
      messager.printError(error.getMessage(), type);
    } else {
      var trace = new StringWriter();
      error.printStackTrace(new PrintWriter(trace));
      messager.printError(trace.toString(), type);
    }
  }

  /// Includes inherited abstract methods in the compiler's member order.
  static List<ExecutableElement> abstractMethods(
      TypeElement type, Elements elements) {
    return ElementFilter.methodsIn(elements.getAllMembers(type)).stream()
        .filter(method -> method.getModifiers().contains(Modifier.ABSTRACT))
        .toList();
  }

  static boolean validateSingleGenerator(
      ProcessingEnvironment processingEnv,
      TypeElement source, Class<? extends Annotation> annotation) {
    var annotations = source.getAnnotationMirrors().stream()
        .map(value -> value.getAnnotationType().toString())
        .filter(GENERATORS::contains)
        .sorted()
        .toList();
    if (annotations.size() <= 1) return true;

    if (annotations.getFirst().equals(annotation.getCanonicalName())) {
      processingEnv.getMessager().printError(
          "Only one generating annotation is allowed: "
              + annotations.stream()
                  .map(ProcessorUtils::simpleAnnotation)
                  .collect(joining(", ")),
          source);
    }
    return false;
  }

  static boolean validateGeneratedNamesAvailable(
      ProcessingEnvironment processingEnv,
      TypeElement source,
      String... classNames) {
    var valid = true;
    var elements = processingEnv.getElementUtils();
    for (var className : classNames) {
      var existing = elements.getTypeElement(className);
      if (existing == null) continue;

      processingEnv.getMessager().printError(
          "Generated class name conflicts with existing type "
              + existing.getQualifiedName() + ": " + className,
          source);
      valid = false;
    }
    return valid;
  }

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
    validateUserIdentifiers(element, element.getEnclosedElements());
  }

  static void validateUserIdentifiers(
      TypeElement element, Iterable<? extends Element> members)
      throws ProcessorError {
    if (isAlveoloGeneratedSpecification(element)) return;

    validateUserIdentifier(element, element.getSimpleName().toString(),
        "type");

    for (var component : element.getRecordComponents()) {
      validateUserIdentifier(component, component.getSimpleName().toString(),
          "record component");
    }

    for (var member : members) {
      if (!(member instanceof ExecutableElement method)
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

  static String callbackClassName(TypeElement element, Elements elements) {
    return qualifyName(packageName(element, elements),
        callbackSimpleClassName(element));
  }

  static String callbackSimpleClassName(TypeElement element) {
    var callback = element.getAnnotation(Callback.class);
    var override = callback == null ? "" : callback.name();

    return generatedSimpleClassName(element, override);
  }

  static String foreignMemorySimpleClassName(TypeElement element) {
    var callState = element.getAnnotation(CallState.class);
    if (callState != null)
      return generatedSimpleClassName(element, callState.name());

    var struct = element.getAnnotation(Struct.class);
    if (struct != null)
      return generatedSimpleClassName(element, struct.name());

    var union = element.getAnnotation(Union.class);
    if (union != null)
      return generatedSimpleClassName(element, union.name());

    return generatedSimpleClassName(element, "");
  }

  static String foreignInterfaceClassName(
      TypeElement element, Elements elements) {
    String simpleName = foreignInterfaceSimpleClassName(element);
    return qualifyName(packageName(element, elements), simpleName);
  }

  static String foreignInterfaceSimpleClassName(TypeElement element) {
    var foreignInterface = element.getAnnotation(ForeignInterface.class);
    String override = foreignInterface == null ? "" : foreignInterface.name();

    return generatedSimpleClassName(element, override);
  }

  static String dispatchTableClassName(TypeElement element, Elements elements) {
    String simpleName = dispatchTableSimpleClassName(element);
    return qualifyName(packageName(element, elements), simpleName);
  }

  static String dispatchTableSimpleClassName(TypeElement element) {
    var dispatchTable = element.getAnnotation(DispatchTable.class);
    var override = dispatchTable == null ? "" : dispatchTable.name();

    return generatedSimpleClassName(element, override);
  }

  static String vtableSpecificationSimpleClassName(TypeElement element) {
    var struct = element.getAnnotation(Struct.class);
    if (struct != null && !struct.name().isEmpty())
      return struct.name() + "V";

    var sourceName = element.getSimpleName().toString();
    return hasSpecSuffix(element)
        ? sourceName.substring(0, sourceName.length() - "Spec".length())
            + "VSpec"
        : sourceName + "V";
  }

  static String vtableImplementationSimpleClassName(TypeElement element) {
    var specification = vtableSpecificationSimpleClassName(element);
    return specification.endsWith("Spec")
        ? specification.substring(0,
            specification.length() - "Spec".length())
        : specification + "F";
  }

  static String packageName(TypeElement element, Elements elements) {
    return elements.getPackageOf(element).getQualifiedName().toString();
  }

  static String generatedTypeVisibility(TypeElement source) {
    return source.getModifiers().contains(Modifier.PUBLIC) ? "public " : "";
  }

  static String sourceMethodSignature(ExecutableElement method) {
    return "public " + sourceType(method.getReturnType()) + " "
        + method.getSimpleName()
        + method.getParameters().stream()
            .map(ProcessorUtils::sourceParameter)
            .collect(joining(",\n      ", "(\n      ", ")"));
  }

  static String sourceParameter(VariableElement parameter) {
    return sourceType(parameter.asType()) + " " + parameter.getSimpleName();
  }

  private static String sourceType(TypeMirror type) {
    return switch (type.getKind()) {
      case BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, VOID ->
        type.getKind().name().toLowerCase();
      case ARRAY -> sourceType(((ArrayType) type).getComponentType()) + "[]";
      case DECLARED, ERROR -> {
        var declared = (DeclaredType) type;
        var element = (TypeElement) declared.asElement();
        var enclosing = declared.getEnclosingType();
        var name = enclosing.getKind() == TypeKind.NONE
            ? element.getQualifiedName().toString()
            : sourceType(enclosing) + "." + element.getSimpleName();
        var arguments = declared.getTypeArguments();
        yield arguments.isEmpty() ? name : arguments.stream()
            .map(ProcessorUtils::sourceType)
            .collect(joining(", ", name + "<", ">"));
      }
      default -> type.toString();
    };
  }

  static String osArray(Library.OS[] oses) {
    return Arrays.stream(oses)
        .map(os -> "org.alveolo.ffm.Library.OS." + os.name())
        .collect(joining(", ",
            "new org.alveolo.ffm.Library.OS[] {", "}"));
  }

  static String qualifyName(String packageName, String className) {
    return packageName.isEmpty() ? className : packageName + "." + className;
  }

  private static String generatedSimpleClassName(
      TypeElement element, String override) {
    if (!override.isEmpty()) return override;

    var sourceName = element.getSimpleName().toString();
    return hasSpecSuffix(element)
        ? sourceName.substring(0, sourceName.length() - "Spec".length())
        : sourceName + "F";
  }

  private static String simpleAnnotation(String name) {
    return "@" + name.substring(name.lastIndexOf('.') + 1);
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

  static boolean isAlveoloGeneratedSpecification(
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

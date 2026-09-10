package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_25;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemorySimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.reportError;
import static org.alveolo.ffm.processor.ProcessorUtils.validateGeneratedClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;
import static org.alveolo.ffm.processor.ProcessorUtils.validateUserIdentifiers;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.alveolo.ffm.Fields;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;
import org.alveolo.ffm.Virtual;

@SupportedAnnotationTypes({
  "org.alveolo.ffm.Struct",
  "org.alveolo.ffm.Union",
  "org.alveolo.ffm.Fields",
  "org.alveolo.ffm.Virtual",
})
@SupportedSourceVersion(RELEASE_25)
public class ForeignMemoryProcessor extends AbstractProcessor {
  private final LayoutCycleValidator layoutCycles = new LayoutCycleValidator();

  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) return true;

    var generatedTypes = GeneratedTypeRegistry.create(processingEnv, roundEnv);
    var generator = new ForeignMemoryGenerator(
        processingEnv, generatedTypes, layoutCycles);
    var processedTypes = new HashSet<TypeElement>();

    for (var annotation : annotations) {
      if (annotation.getQualifiedName().contentEquals(
          Virtual.class.getCanonicalName())) {
        validateVirtualAnnotations(roundEnv.getElementsAnnotatedWith(
            annotation));
        continue;
      }

      if (annotation.getQualifiedName().contentEquals(
          Fields.class.getCanonicalName())) {
        validateFieldsAnnotations(roundEnv.getElementsAnnotatedWith(
            annotation));
        continue;
      }

      for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element instanceof TypeElement type && processedTypes.add(type))
          processType(annotation, type, generator);
      }
    }

    if (!roundEnv.errorRaised())
      layoutCycles.validate(processingEnv.getMessager());
    return true;
  }

  private void processType(TypeElement annotation, TypeElement type,
      ForeignMemoryGenerator generator) {
    var messager = processingEnv.getMessager();
    var struct = type.getAnnotation(Struct.class);
    var union = type.getAnnotation(Union.class);
    if (struct != null && union != null) {
      messager.printError("@Struct and @Union cannot be used on the same type",
          type);
      return;
    }

    switch (type.getKind()) {
      case INTERFACE, RECORD -> {
        try {
          if (type.getAnnotation(Fields.class) != null
              && (struct != null || union != null)) return;

          if (struct != null) {
            validateSimpleClassName(type, struct, struct.name());
            validateGeneratedClassName(type, struct,
                foreignMemorySimpleClassName(type));
            if (type.getKind() == ElementKind.INTERFACE) {
              validateUserIdentifiers(type,
                  processingEnv.getElementUtils().getAllMembers(type));
            } else {
              validateUserIdentifiers(type);
            }
            validateTopLevelType(type, struct);
            if (struct.vtable()
                && type.getKind() == ElementKind.RECORD) {
              messager.printError(
                  "@Struct(vtable = true) can only be applied to an "
                      + "interface, not RECORD",
                  type);
            } else {
              generator.write(type, "struct", struct.vtable());
            }
          }

          if (union != null) {
            validateSimpleClassName(type, union, union.name());
            validateGeneratedClassName(type, union,
                foreignMemorySimpleClassName(type));
            validateUserIdentifiers(type);
            validateTopLevelType(type, union);
            if (type.getKind() == ElementKind.RECORD) {
              messager.printError("@" + annotation.getSimpleName()
                  + " can only be applied to an interface, not "
                  + ElementKind.RECORD, type);
            } else {
              generator.write(type, "union", false);
            }
          }
        } catch (Throwable e) {
          reportError(messager, e, type);
        }
      }
      default -> messager.printError("@" + annotation.getSimpleName()
          + " can only be applied to an interface, not " + type.getKind(),
          type);
    }
  }

  private void validateVirtualAnnotations(Set<? extends Element> elements) {
    var messager = processingEnv.getMessager();

    for (var element : elements) {
      if (!(element instanceof ExecutableElement method)
          || method.getKind() != ElementKind.METHOD
          || !method.getModifiers().contains(ABSTRACT)
          || method.getModifiers().contains(STATIC)
          || method.getModifiers().contains(DEFAULT)) {
        messager.printError(
            "@Virtual is only allowed on abstract instance methods", element);
      }
    }
  }

  private void validateFieldsAnnotations(Set<? extends Element> elements) {
    var messager = processingEnv.getMessager();
    var elementUtils = processingEnv.getElementUtils();

    for (var element : elements) {
      if (!(element instanceof TypeElement type)
          || type.getKind() != ElementKind.INTERFACE) {
        messager.printError("@Fields is only allowed on interfaces", element);
        continue;
      }
      if (type.getAnnotation(Struct.class) != null
          || type.getAnnotation(Union.class) != null) {
        messager.printError(
            "@Fields is not allowed on @Struct or @Union interfaces", type);
        continue;
      }

      var seen = new HashSet<String>();
      for (var name : type.getAnnotation(Fields.class).value()) {
        if (!seen.add(name)) {
          messager.printError("Duplicate @Fields name: " + name, type);
          continue;
        }

        var found = elementUtils.getAllMembers(type).stream()
            .filter(ExecutableElement.class::isInstance)
            .map(ExecutableElement.class::cast)
            .filter(method -> method.getModifiers().contains(ABSTRACT))
            .filter(method -> !method.getModifiers().contains(STATIC))
            .filter(method -> !method.getModifiers().contains(DEFAULT))
            .anyMatch(method -> method.getSimpleName().contentEquals(name));
        if (!found)
          messager.printError("@Fields method not found: " + name, type);
      }
    }
  }
}

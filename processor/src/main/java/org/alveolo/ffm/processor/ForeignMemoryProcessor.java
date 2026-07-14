package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_25;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemorySimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateGeneratedClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;
import static org.alveolo.ffm.processor.ProcessorUtils.validateUserIdentifiers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;
import org.alveolo.ffm.Virtual;

@SupportedAnnotationTypes({
  "org.alveolo.ffm.Struct",
  "org.alveolo.ffm.Union",
  "org.alveolo.ffm.Virtual",
})
@SupportedSourceVersion(RELEASE_25)
public class ForeignMemoryProcessor extends AbstractProcessor {
  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) return true;

    var messager = processingEnv.getMessager();
    var generatedTypes = GeneratedTypeRegistry.create(processingEnv, roundEnv);
    var generator = new ForeignMemoryGenerator(processingEnv, generatedTypes);

    for (var annotation : annotations) {
      if (annotation.getQualifiedName().contentEquals(
          Virtual.class.getCanonicalName())) {
        validateVirtualAnnotations(roundEnv.getElementsAnnotatedWith(
            annotation));
        continue;
      }

      for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element instanceof TypeElement type) {
          switch (type.getKind()) {
            case INTERFACE:
            case RECORD:
              try {
                var struct = type.getAnnotation(Struct.class);
                if (struct != null) {
                  validateSimpleClassName(type, struct, struct.name());
                  validateGeneratedClassName(type, struct,
                      foreignMemorySimpleClassName(type));
                  validateUserIdentifiers(type);
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

                var union = type.getAnnotation(Union.class);
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
              } catch (ProcessorError e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                    e.getMessage(), e.getElement());
              } catch (Throwable e) {
                var sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                messager.printError(sw.toString(), type);
              }
              break;
            case ElementKind kind:
              messager.printError("@" + annotation.getSimpleName()
                  + " can only be applied to an interface, not " + kind, type);
          }
        }
      }
    }

    return true;
  }

  private void validateVirtualAnnotations(Set<? extends Element> elements) {
    var messager = processingEnv.getMessager();

    for (var element : elements) {
      if (!(element instanceof ExecutableElement method)
          || !(method.getEnclosingElement() instanceof TypeElement owner)) {
        messager.printError(
            "@Virtual is only allowed on methods of @Struct(vtable = true)",
            element);
        continue;
      }

      var struct = owner.getAnnotation(Struct.class);
      if (struct == null || !struct.vtable()) {
        messager.printError(
            "@Virtual is only allowed on @Struct(vtable = true) methods",
            method);
        continue;
      }

      if (method.getKind() != ElementKind.METHOD
          || !method.getModifiers().contains(ABSTRACT)
          || method.getModifiers().contains(STATIC)
          || method.getModifiers().contains(DEFAULT)) {
        messager.printError(
            "@Virtual is only allowed on abstract instance methods", method);
      }
    }
  }
}

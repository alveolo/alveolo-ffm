package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_25;
import static org.alveolo.ffm.processor.ProcessorUtils.abstractMethods;
import static org.alveolo.ffm.processor.ProcessorUtils.callbackClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.callbackSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.reportError;
import static org.alveolo.ffm.processor.ProcessorUtils.validateGeneratedClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;
import static org.alveolo.ffm.processor.ProcessorUtils.validateUserIdentifiers;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.Callback;

@SupportedAnnotationTypes("org.alveolo.ffm.Callback")
@SupportedSourceVersion(RELEASE_25)
public class CallbackProcessor extends AbstractProcessor {
  private static final Set<String> CARRIER_ANNOTATIONS = Set.of(
      "org.alveolo.ffm.Address",
      "org.alveolo.ffm.SLong",
      "org.alveolo.ffm.SizeT",
      "org.alveolo.ffm.ULong",
      "org.alveolo.ffm.WCharT");

  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) return true;

    roundEnv.getElementsAnnotatedWith(Callback.class).forEach(element -> {
      if (element instanceof TypeElement type) processType(type);
    });

    return true;
  }

  private void processType(TypeElement type) {
    var messager = processingEnv.getMessager();

    try {
      var annotation = type.getAnnotation(Callback.class);
      if (annotation == null) return;

      if (type.getKind() != ElementKind.INTERFACE) {
        messager.printError(
            "@Callback can only be applied to an interface, not "
                + type.getKind(),
            type);
        return;
      }

      validateSimpleClassName(type, annotation, annotation.name());
      validateGeneratedClassName(type, annotation,
          callbackSimpleClassName(type));
      validateUserIdentifiers(type);
      validateTopLevelType(type, annotation);

      var method = validate(type);
      if (method != null) write(type, method);
    } catch (Throwable e) {
      reportError(messager, e, type);
    }
  }

  private ExecutableElement validate(TypeElement type) {
    var methods = abstractMethods(type, processingEnv.getElementUtils());
    if (methods.size() != 1) {
      processingEnv.getMessager().printError(
          "@Callback interface must have exactly one abstract method", type);
      return null;
    }

    var method = methods.getFirst();
    var valid = validateType(method.getReturnType(), method, true);
    for (var parameter : method.getParameters()) {
      valid &= validateType(parameter.asType(), parameter, false);
    }
    return valid ? method : null;
  }

  private boolean validateType(
      TypeMirror type, Element element, boolean allowVoid) {
    if (type.getAnnotationMirrors().stream()
        .map(annotation -> annotation.getAnnotationType().toString())
        .anyMatch(CARRIER_ANNOTATIONS::contains)) {
      processingEnv.getMessager().printError(
          "@Callback methods do not yet support native carrier annotations",
          element);
      return false;
    }

    if (type.getKind() == TypeKind.VOID && allowVoid
        || type.getKind().isPrimitive()
        || isMemorySegment(type)) {
      return true;
    }

    processingEnv.getMessager().printError(
        "@Callback methods only support primitive types and MemorySegment"
            + (allowVoid ? "; void is also supported as a return type" : ""),
        element);
    return false;
  }

  private boolean isMemorySegment(TypeMirror type) {
    var memorySegment = processingEnv.getElementUtils()
        .getTypeElement(MemorySegment.class.getCanonicalName());
    return processingEnv.getTypeUtils().isSameType(
        type, memorySegment.asType());
  }

  private void write(TypeElement type, ExecutableElement method)
      throws IOException {
    var elements = processingEnv.getElementUtils();
    var packageName = packageName(type, elements);
    var className = callbackClassName(type, elements);
    var simpleClassName = callbackSimpleClassName(type);
    var sourceName = type.getSimpleName().toString();

    var file = processingEnv.getFiler().createSourceFile(className, type);
    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write(
          """
              @javax.annotation.processing.Generated(
                  "<generator>")
              public final class <class> {
                private static final java.lang.foreign.Linker Linker$F =
                    java.lang.foreign.Linker.nativeLinker();

                public static final java.lang.foreign.FunctionDescriptor
                    FunctionDescriptor$F = <descriptor>;

                private static final java.lang.invoke.MethodHandle MethodHandle$F =
                    methodHandle$F();

                private <class>() {}

                public static java.lang.foreign.MemorySegment upcallStub$F(
                    java.lang.foreign.Arena arena, <source> callback) {
                  java.util.Objects.requireNonNull(callback);
                  return Linker$F.upcallStub(
                      MethodHandle$F.bindTo(callback), FunctionDescriptor$F, arena);
                }

                private static java.lang.invoke.MethodHandle methodHandle$F() {
                  try {
                    return java.lang.invoke.MethodHandles.lookup().findVirtual(
                        <source>.class,
                        "<method>",
                        FunctionDescriptor$F.toMethodType());
                  } catch (ReflectiveOperationException e) {
                    throw new ExceptionInInitializerError(e);
                  }
                }
              }
              """
              .replace("<generator>", getClass().getCanonicalName())
              .replace("<class>", simpleClassName)
              .replace("<source>", sourceName)
              .replace("<descriptor>", descriptor(method))
              .replace("<method>", method.getSimpleName().toString()));
    }
  }

  private String descriptor(ExecutableElement method) {
    var layouts = new ArrayList<String>();
    var returnsVoid = method.getReturnType().getKind() == TypeKind.VOID;
    if (!returnsVoid) layouts.add(layout(method.getReturnType()));
    method.getParameters().stream()
        .map(parameter -> layout(parameter.asType()))
        .forEach(layouts::add);

    var factory = "java.lang.foreign.FunctionDescriptor."
        + (returnsVoid ? "ofVoid" : "of");
    return layouts.isEmpty()
        ? factory + "()"
        : factory + "(\n      " + String.join(",\n      ", layouts) + ")";
  }

  private String layout(TypeMirror type) {
    if (isMemorySegment(type))
      return "java.lang.foreign.ValueLayout.ADDRESS";

    var layout = TypeGenerator.primitiveLayout(type.getKind());
    if (layout != null) return layout;

    throw new IllegalArgumentException("Unsupported callback type: " + type);
  }
}

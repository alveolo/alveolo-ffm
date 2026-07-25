package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.RELEASE_25;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemoryClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemorySimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.osArray;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.quote;
import static org.alveolo.ffm.processor.ProcessorUtils.validateGeneratedClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;
import static org.alveolo.ffm.processor.ProcessorUtils.validateUserIdentifiers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;

import org.alveolo.ffm.CallState;

@SupportedAnnotationTypes("org.alveolo.ffm.CallState")
@SupportedSourceVersion(RELEASE_25)
public class CallStateProcessor extends AbstractProcessor {
  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) return true;

    roundEnv.getElementsAnnotatedWith(CallState.class)
        .forEach(element -> {
          if (element instanceof TypeElement type) {
            processType(type);
          }
        });

    return true;
  }

  private void processType(TypeElement type) {
    var messager = processingEnv.getMessager();

    try {
      var annotation = type.getAnnotation(CallState.class);
      if (annotation == null) return;

      if (type.getKind() != ElementKind.INTERFACE) {
        messager.printError(
            "@CallState can only be applied to an interface, not "
                + type.getKind(),
            type);
        return;
      }

      validateSimpleClassName(type, annotation, annotation.name());
      validateGeneratedClassName(type, annotation,
          foreignMemorySimpleClassName(type));
      validateUserIdentifiers(type);
      validateTopLevelType(type, annotation);

      var accessor = validate(type, annotation);
      if (accessor != null) {
        write(type, annotation, accessor);
      }
    } catch (ProcessorError e) {
      messager.printMessage(Diagnostic.Kind.ERROR,
          e.getMessage(), e.element);
    } catch (Throwable e) {
      var sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      messager.printError(sw.toString(), type);
    }
  }

  private ExecutableElement validate(
      TypeElement type, CallState callState) {
    var messager = processingEnv.getMessager();
    var valid = true;

    if (callState.value().isBlank()) {
      messager.printError("@CallState value must not be blank", type);
      valid = false;
    }

    for (var override : callState.overrides()) {
      if (override.value().isBlank()) {
        messager.printError(
            "@CallState.Override value must not be blank", type);
        valid = false;
      }
    }

    var accessors = type.getEnclosedElements().stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(method -> method.getKind() == ElementKind.METHOD)
        .filter(method -> method.getModifiers().contains(Modifier.ABSTRACT))
        .toList();

    if (accessors.size() != 1) {
      messager.printError(
          "@CallState interface must declare exactly one abstract accessor",
          type);
      return null;
    }

    var accessor = accessors.getFirst();
    if (!accessor.getParameters().isEmpty()
        || accessor.getReturnType().getKind() != TypeKind.INT) {
      messager.printError(
          "@CallState accessor must have no parameters and return int",
          accessor);
      valid = false;
    }

    return valid ? accessor : null;
  }

  private void write(TypeElement type, CallState callState,
      ExecutableElement accessor) throws IOException {
    var elements = processingEnv.getElementUtils();
    var packageName = packageName(type, elements);
    var className = foreignMemoryClassName(type, elements);
    var simpleClassName = foreignMemorySimpleClassName(type);
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
              public final class <class> implements <source> {
                public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
                    java.lang.foreign.Linker.Option.captureStateLayout();

                public static final String StateName$F =
                    org.alveolo.ffm.ForeignUtils.callStateName(
                        <state><overrides>);

                public static final java.lang.foreign.Linker.Option LinkerOption$F =
                    java.lang.foreign.Linker.Option.captureCallState(StateName$F);

                public static java.lang.foreign.MemorySegment allocate$F(
                    java.lang.foreign.SegmentAllocator allocator) {
                  return allocator.allocate(
                      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
                }

                public static <class> reinterpret$F(
                    java.lang.foreign.MemorySegment memorySegment) {
                  return new <class>(memorySegment.reinterpret(
                      MemoryLayout$F.byteSize()));
                }

                public final java.lang.foreign.MemorySegment MemorySegment$F;

                public <class>(java.lang.foreign.SegmentAllocator allocator) {
                  this(allocate$F(allocator));
                }

                public <class>(java.lang.foreign.MemorySegment memorySegment) {
                  this.MemorySegment$F = memorySegment;
                }

                public static final java.lang.foreign.MemoryLayout.PathElement
                    <accessor>$PathElement$F = java.lang.foreign.MemoryLayout
                        .PathElement.groupElement(StateName$F);

                public static final java.lang.invoke.VarHandle <accessor>$VarHandle$F =
                    java.lang.invoke.MethodHandles.insertCoordinates(
                        MemoryLayout$F.varHandle(<accessor>$PathElement$F), 1, 0L);

                public int <accessor>() {
                  return (int) <accessor>$VarHandle$F.get(MemorySegment$F);
                }
              }
              """
              .replace("<generator>", getClass().getCanonicalName())
              .replace("<class>", simpleClassName)
              .replace("<source>", sourceName)
              .replace("<state>", quote(callState.value()))
              .replace("<overrides>", overrides(callState))
              .replace("<accessor>", accessor.getSimpleName().toString()));
    }
  }

  private String overrides(CallState callState) {
    var overrides = callState.overrides();
    if (overrides.length == 0) return "";

    return Arrays.stream(overrides)
        .map(this::callStateOverride)
        .map(override -> override.indent(10).stripTrailing())
        .collect(joining(",\n", ",\n", ""));
  }

  private String callStateOverride(CallState.Override override) {
    return """
        new org.alveolo.ffm.ForeignUtils.CallStateOverride(
            <os>,
            <value>)
        """
        .replace("<os>", osArray(override.os()))
        .replace("<value>", quote(override.value()))
        .stripTrailing();
  }
}

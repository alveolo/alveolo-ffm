package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_25;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemoryClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemorySimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateGeneratedClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;
import static org.alveolo.ffm.processor.ProcessorUtils.validateUserIdentifiers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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
import org.alveolo.ffm.Library;

@SupportedAnnotationTypes("org.alveolo.ffm.CallState")
@SupportedSourceVersion(RELEASE_25)
public class CallStateProcessor extends AbstractProcessor {
  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) return true;

    var messager = processingEnv.getMessager();
    for (var annotation : annotations) {
      for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (!(element instanceof TypeElement type)) continue;

        try {
          var callState = type.getAnnotation(CallState.class);
          if (callState == null) continue;

          if (type.getKind() != ElementKind.INTERFACE) {
            messager.printError(
                "@CallState can only be applied to an interface, not "
                    + type.getKind(),
                type);
            continue;
          }

          validateSimpleClassName(type, callState, callState.name());
          validateGeneratedClassName(type, callState,
              foreignMemorySimpleClassName(type));
          validateUserIdentifiers(type);
          validateTopLevelType(type, callState);

          if (!validate(type, callState)) continue;
          write(type, callState);
        } catch (ProcessorError e) {
          messager.printMessage(Diagnostic.Kind.ERROR,
              e.getMessage(), e.getElement());
        } catch (Throwable e) {
          var sw = new StringWriter();
          e.printStackTrace(new PrintWriter(sw));
          messager.printError(sw.toString(), type);
        }
      }
    }

    return true;
  }

  private boolean validate(TypeElement type, CallState callState) {
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

    var accessors = new ArrayList<ExecutableElement>();
    for (var member : type.getEnclosedElements()) {
      if (member instanceof ExecutableElement method
          && method.getKind() == ElementKind.METHOD
          && method.getModifiers().contains(Modifier.ABSTRACT)) {
        accessors.add(method);
      }
    }

    if (accessors.size() != 1) {
      messager.printError(
          "@CallState interface must declare exactly one abstract accessor",
          type);
      return false;
    }

    var accessor = accessors.getFirst();
    if (!accessor.getParameters().isEmpty()
        || accessor.getReturnType().getKind() != TypeKind.INT) {
      messager.printError(
          "@CallState accessor must have no parameters and return int",
          accessor);
      valid = false;
    }

    return valid;
  }

  private void write(TypeElement type, CallState callState)
      throws IOException {
    var elements = processingEnv.getElementUtils();
    var packageName = packageName(type, elements);
    var className = foreignMemoryClassName(type, elements);
    var simpleClassName = foreignMemorySimpleClassName(type);
    var sourceName = type.getSimpleName().toString();
    var accessor = type.getEnclosedElements().stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(method -> method.getModifiers().contains(Modifier.ABSTRACT))
        .findFirst()
        .orElseThrow();

    var file = processingEnv.getFiler().createSourceFile(className, type);
    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
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
    var result = new StringBuilder();
    for (var override : callState.overrides()) {
      result.append(",\n")
          .append(callStateOverride(override)
              .indent(10)
              .stripTrailing());
    }

    return result.toString();
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

  private String osArray(Library.OS[] oses) {
    var result = new StringBuilder("new org.alveolo.ffm.Library.OS[] {");
    for (var i = 0; i < oses.length; i++) {
      if (i > 0) result.append(", ");
      result.append("org.alveolo.ffm.Library.OS.")
          .append(oses[i].name());
    }
    return result.append("}").toString();
  }

  private static String quote(String value) {
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
        + "\"";
  }
}

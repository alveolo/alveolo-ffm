package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_25;
import static org.alveolo.ffm.processor.ProcessorUtils.dispatchTableClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.dispatchTableSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateGeneratedClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;
import static org.alveolo.ffm.processor.ProcessorUtils.validateUserIdentifiers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.Slot;

@SupportedAnnotationTypes("org.alveolo.ffm.DispatchTable")
@SupportedSourceVersion(RELEASE_25)
public class DispatchTableProcessor extends AbstractProcessor {
  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var messager = processingEnv.getMessager();

    if (roundEnv.processingOver()) return true;

    var generatedTypes = GeneratedTypeRegistry.create(processingEnv, roundEnv);
    for (var annotation : annotations) {
      var elements = roundEnv.getElementsAnnotatedWith(annotation);

      for (var element : elements) {
        if (element instanceof TypeElement type) {
          try {
            var dt = type.getAnnotation(DispatchTable.class);
            if (dt != null) {
              validateSimpleClassName(type, dt, dt.name());
              validateGeneratedClassName(type, dt,
                  dispatchTableSimpleClassName(type));
              validateUserIdentifiers(type);
              validateTopLevelType(type, dt);
              writeFile(type, generatedTypes);
            }
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
    }

    return true;
  }

  private void writeFile(TypeElement iface,
      GeneratedTypeRegistry generatedTypes) throws ProcessorError, IOException {
    var messager = processingEnv.getMessager();

    if (iface.getKind() != ElementKind.INTERFACE) {
      messager.printError("@DispatchTable is only allowed on interfaces",
          iface);
      return;
    }

    var methods = abstractMethods(iface);
    if (!validateSlots(methods)) return;

    var elements = processingEnv.getElementUtils();
    String packageName = packageName(iface, elements);
    String ifaceSimpleName = iface.getSimpleName().toString();
    String className = dispatchTableClassName(iface, elements);
    String simpleClassName = dispatchTableSimpleClassName(iface);

    var file = processingEnv.getFiler().createSourceFile(className, iface);

    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
          @javax.annotation.processing.Generated(
              "<generator>")
          public final class <name> implements <interface> {
            private static final java.lang.foreign.Linker Linker$F =
                java.lang.foreign.Linker.nativeLinker();

            public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
                java.lang.foreign.MemoryLayout.sequenceLayout(<slotCount>L,
                    java.lang.foreign.ValueLayout.ADDRESS);

            public static <name> reinterpret$F(
                java.lang.foreign.MemorySegment memorySegment) {
              return new <name>(memorySegment.reinterpret(
                  MemoryLayout$F.byteSize()));
            }

            public final java.lang.foreign.MemorySegment MemorySegment$F;

          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<name>", simpleClassName)
          .replace("<interface>", ifaceSimpleName)
          .replace("<slotCount>", Long.toString(slotCount(methods))));

      var generators = generators(methods, generatedTypes);
      writeConstructor(out, simpleClassName, generators);

      for (var i = 0; i < generators.size(); i++) {
        var generator = generators.get(i);
        if (!generator.hasErrors) {
          writeMethodDescriptor(out, generator, i);
        }
        out.write(generator.methodWithHandle());
      }

      out.write("}\n");
    }
  }

  private List<ExecutableElement> abstractMethods(TypeElement type) {
    return type.getEnclosedElements().stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(method -> method.getKind() == ElementKind.METHOD)
        .filter(method -> method.getModifiers().contains(Modifier.ABSTRACT))
        .toList();
  }

  private boolean validateSlots(List<ExecutableElement> methods) {
    var valid = true;
    var slotIndexes = new HashMap<Integer, ExecutableElement>();

    for (var method : methods) {
      var slot = method.getAnnotation(Slot.class);
      if (slot == null) {
        processingEnv.getMessager().printError(
            "@Slot is required on @DispatchTable methods", method);
        valid = false;
        continue;
      }

      var slotIndex = slot.value();
      if (slotIndex < 0) {
        processingEnv.getMessager().printError(
            "@Slot value must be non-negative", method);
        valid = false;
        continue;
      }

      var previous = slotIndexes.putIfAbsent(slotIndex, method);
      if (previous != null) {
        processingEnv.getMessager().printError(
            "Duplicate @Slot index: " + slotIndex, method);
        processingEnv.getMessager().printError(
            "Duplicate @Slot index: " + slotIndex, previous);
        valid = false;
      }
    }

    return valid;
  }

  private long slotCount(List<ExecutableElement> methods) {
    return methods.stream()
        .mapToLong(this::slot)
        .max()
        .orElse(-1L) + 1L;
  }

  private List<ExecutableGenerator> generators(
      List<ExecutableElement> methods, GeneratedTypeRegistry generatedTypes) {
    return IntStream.range(0, methods.size())
        .mapToObj(index -> new ExecutableGenerator(
            processingEnv, generatedTypes, methods.get(index),
            "MethodHandle$" + index + "$F", true))
        .toList();
  }

  private void writeMethodDescriptor(Writer out,
      ExecutableGenerator generator, int index) throws IOException {
    out.write("""

          private static final java.lang.invoke.MethodHandle <mh> =
              Linker$F.downcallHandle(
              <descriptor>);
        """
        .replace("<mh>", "DowncallHandle$" + index + "$F")
        .replace("<descriptor>", generator.descriptor()));
  }

  private void writeConstructor(Writer out, String className,
      List<ExecutableGenerator> generators) throws IOException {
    out.write("""
          public <class>(java.lang.foreign.MemorySegment memorySegment) {
            this.MemorySegment$F = memorySegment;
        <initializers>
          }
        """
        .replace("<class>", className)
        .replace("<initializers>", methodHandleInitializers(generators)
            .indent(4)
            .stripTrailing()));
  }

  private String methodHandleInitializers(
      List<ExecutableGenerator> generators) {
    var result = new StringBuilder();
    for (var i = 0; i < generators.size(); i++) {
      var generator = generators.get(i);

      if (generator.hasErrors) {
        continue;
      }

      result.append("""
          this.<mh> = DowncallHandle$<index>$F.bindTo(
              MemorySegment$F.getAtIndex(
                  java.lang.foreign.ValueLayout.ADDRESS, <slot>L));
          """
          .replace("<mh>", generator.methodHandleName)
          .replace("<index>", Integer.toString(i))
          .replace("<slot>", Integer.toString(slot(generator.element))));
    }
    return result.toString();
  }

  private int slot(ExecutableElement method) {
    return method.getAnnotation(Slot.class).value();
  }
}

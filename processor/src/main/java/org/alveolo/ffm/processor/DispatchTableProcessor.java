package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_25;
import static org.alveolo.ffm.processor.ProcessorUtils.dispatchTableClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.dispatchTableSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

    for (var annotation : annotations) {
      var elements = roundEnv.getElementsAnnotatedWith(annotation);

      for (var element : elements) {
        if (element instanceof TypeElement type) {
          try {
            var dt = type.getAnnotation(DispatchTable.class);
            if (dt != null) {
              validateSimpleClassName(annotation, dt, dt.name());
              writeFile(type);
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

  private void writeFile(TypeElement iface) throws ProcessorError, IOException {
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

    try (var out = file.openWriter()) {
      if (!packageName.isEmpty()) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
          import java.lang.foreign.*;
          import java.lang.invoke.MethodHandle;

          @javax.annotation.processing.Generated(
              "<generator>")
          public final class <name> implements <interface> {
            private static final Linker FF$LINKER = Linker.nativeLinker();

            public static final MemoryLayout FD$LAYOUT =
                MemoryLayout.sequenceLayout(<slotCount>L, ValueLayout.ADDRESS);

            public static <name> reinterpret(MemorySegment ms) {
              return new <name>(ms.reinterpret(FD$LAYOUT.byteSize()));
            }

            public final MemorySegment ms;

          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<name>", simpleClassName)
          .replace("<interface>", ifaceSimpleName)
          .replace("<slotCount>", Long.toString(slotCount(methods))));

      var generators = generators(methods);
      writeConstructor(out, simpleClassName, generators, methods);

      for (var i = 0; i < generators.size(); i++) {
        var generator = generators.get(i);
        writeMethodDescriptor(out, generator, i);
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
    var indexes = new HashMap<Integer, ExecutableElement>();

    for (var method : methods) {
      var slot = method.getAnnotation(Slot.class);
      if (slot == null) {
        processingEnv.getMessager().printError(
            "@Slot is required on @DispatchTable methods", method);
        valid = false;
        continue;
      }

      var index = slot.index();
      var value = slot.value();
      var hasIndex = hasSlotValue(method, "index");
      var hasValue = hasSlotValue(method, "value");
      if (hasIndex == hasValue) {
        processingEnv.getMessager().printError(
            hasIndex
                ? "@Slot value and index cannot both be set"
                : "@Slot index is required",
            method);
        valid = false;
        continue;
      }

      var slotIndex = hasIndex ? index : value;
      if (slotIndex < 0) {
        processingEnv.getMessager().printError(
            "@Slot index must be non-negative", method);
        valid = false;
      }

      var previous = indexes.putIfAbsent(slotIndex, method);
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

  private boolean hasSlotValue(ExecutableElement method, String name) {
    for (var mirror : method.getAnnotationMirrors()) {
      if (!mirror.getAnnotationType().toString()
          .equals(Slot.class.getCanonicalName())) {
        continue;
      }

      for (var entry : mirror.getElementValues().entrySet()) {
        if (entry.getKey().getSimpleName().contentEquals(name)) return true;
      }
    }

    return false;
  }

  private long slotCount(List<ExecutableElement> methods) {
    return methods.stream()
        .mapToLong(this::slot)
        .max()
        .orElse(-1L) + 1L;
  }

  private List<ExecutableGenerator>
      generators(List<ExecutableElement> methods) {
    var index = 0;
    var generators = new java.util.ArrayList<ExecutableGenerator>();
    for (var method : methods) {
      generators.add(new ExecutableGenerator(
          processingEnv, method, "FF$MH$" + index++, true));
    }
    return generators;
  }

  private void writeMethodDescriptor(
      java.io.Writer out, ExecutableGenerator generator, int index)
      throws IOException {
    out.write("""

          private static final MethodHandle <mh> = FF$LINKER.downcallHandle(
              <descriptor>);
        """
        .replace("<mh>", "FF$MD$" + Integer.toString(index))
        .replace("<descriptor>", generator.descriptor()));
  }

  private void writeConstructor(java.io.Writer out, String className,
      List<ExecutableGenerator> generators, List<ExecutableElement> methods)
      throws IOException {
    out.write("""
          public <class>(MemorySegment ms) {
            this.ms = ms;
        <initializers>
          }
        """
        .replace("<class>", className)
        .replace("<initializers>", methodHandleInitializers(generators, methods)
            .indent(4)
            .stripTrailing()));
  }

  private String methodHandleInitializers(List<ExecutableGenerator> generators,
      List<ExecutableElement> methods) {
    var result = new StringBuilder();
    for (var i = 0; i < generators.size(); i++) {
      result.append("""
          this.<mh> = FF$MD$<index>.bindTo(
              ms.getAtIndex(ValueLayout.ADDRESS, <slot>L));
          """
          .replace("<mh>", generators.get(i).methodHandleName)
          .replace("<index>", Integer.toString(i))
          .replace("<slot>", Integer.toString(slot(methods.get(i)))));
    }
    return result.toString().stripTrailing();
  }

  private int slot(ExecutableElement method) {
    var slot = method.getAnnotation(Slot.class);
    return hasSlotValue(method, "index") ? slot.index() : slot.value();
  }
}

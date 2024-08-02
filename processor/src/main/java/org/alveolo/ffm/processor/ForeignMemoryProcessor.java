package org.alveolo.ffm.processor;

import static java.util.Arrays.stream;
import static javax.lang.model.SourceVersion.RELEASE_21;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.Element;
import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.ForeignUnion;

@SupportedAnnotationTypes({
  "org.alveolo.ffm.ForeignStruct",
  "org.alveolo.ffm.ForeignUnion",
})
@SupportedSourceVersion(RELEASE_21)
public class ForeignMemoryProcessor extends AbstractProcessor {
  @Override
  public boolean process(
      Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {
    var messager = processingEnv.getMessager();

    messager.printNote("StructProcessor process...");
    messager.printNote("RoundEnvironment"
        + ": processingOver = " + roundEnv.processingOver()
        + ", rootElements = " + roundEnv.getRootElements());

    if (roundEnv.processingOver()) return true;

    for (var annotation : annotations) {
      messager.printNote("Annotation: " + annotation);

      var elements = roundEnv.getElementsAnnotatedWith(annotation);

      for (var element : elements) {
        messager.printNote("Annotated Element: " + element);

        if (element instanceof PackageElement pkg) {
          try {
            writeFiles(pkg);
          } catch (Throwable e) {
            var sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            messager.printError(sw.toString(), pkg);
          }
        }
      }
    }

    return true;
  }

  private void writeFiles(PackageElement pkg) throws IOException {
    for (var struct : pkg.getAnnotationsByType(ForeignStruct.class)) {
      writeFile(pkg, "structLayout", struct.name(), struct.elements());
    }

    for (var union : pkg.getAnnotationsByType(ForeignUnion.class)) {
      writeFile(pkg, "unionLayout", union.name(), union.elements());
    }
  }

  private void writeFile(PackageElement pkg, String memoryLayout,
      String simpleClassName, Element[] elements) throws IOException {
    String packageName = pkg.getQualifiedName().toString();
    String className = packageName + "." + simpleClassName;

    var file = processingEnv.getFiler().createSourceFile(className, pkg);

    try (var out = file.openWriter()) {
      if (packageName != null) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
          import java.lang.foreign.*;

          @javax.annotation.processing.Generated("<generator>")
          //@org.alveolo.ffm.Value
          public final class <name> {
            public final MemorySegment ms;

            public <name>(MemorySegment ms) {
              this.ms = ms;
            }

            public static final MemoryLayout FM$LAYOUT =
              MemoryLayout.<layout>(
                org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<name>", simpleClassName)
          .replace("<layout>", memoryLayout));

      var elementGens = stream(elements)
          .map(e -> variableGenerator(pkg, e))
          .toList();

      var layoutGen = new MemoryLayoutGenerator(processingEnv, elementGens);

      out.write(layoutGen.layout());

      out.write("""
                }));

            public static <name> allocate(SegmentAllocator allocator) {
              return new <name>(allocator.allocate(
                FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment()));
            }
          """
          .replace("<name>", simpleClassName));

      for (var e : elements) {
        writeAccessors(out, e);
      }

      out.write("}\n");
    }
  }

  private VariableGenerator variableGenerator(
      PackageElement pkg, Element element) {
    var typeMirror = getTypeMirror(element, Element::type);
    long sequence = element.sequence();
    if (sequence > 1) {
      typeMirror = processingEnv.getTypeUtils().getArrayType(typeMirror);
    }

    return new VariableGenerator(processingEnv,
        typeMirror, sequence, element.name(), pkg);
  }

  private void writeAccessors(Writer out, Element element) throws IOException {
    String name = element.name();

    out.write("""

          private static final MemoryLayout.PathElement FM$PE$<name> =
            MemoryLayout.PathElement.groupElement("<name>");

        """
        .replace("<name>", name));

    var typeMirror = getTypeMirror(element, Element::type);
    String type = typeMirror.toString();

    var sequence = element.sequence();
    if (sequence > 1) {
      writeAccessorsBuffer(out, name, type, sequence);
    } else {
      writeAccessorsSimple(out, type, name);
    }
  }

  private static <T extends Annotation> TypeMirror getTypeMirror(
      T annotation, Function<T, Class<?>> getter) {
    try {
      getter.apply(annotation);
      return null;
    } catch (MirroredTypeException mte) {
      return mte.getTypeMirror();
    }
  }

  private void writeAccessorsSimple(Writer out, String type, String name)
      throws IOException {
    out.write("""
          public static final java.lang.invoke.VarHandle FM$VH$<name> =
            FM$LAYOUT.varHandle(FM$PE$<name>);

          public <type> <name>() {
            return (<type>) FM$VH$<name>.get(ms);
          }

          public void <name>(<type> value) {
            FM$VH$<name>.set(ms, value);
          }
        """
        .replace("<name>", name)
        .replace("<type>", type));
  }

  private void writeAccessorsBuffer(
      Writer out, String name, String type, long size) throws IOException {
    var capType = capitalize(type);

    out.write("""
          public static final long FM$OFFSET$<name> =
              FM$LAYOUT.byteOffset(FM$PE$<name>);

          public static final long FM$SIZE$<name> =
              FM$LAYOUT.select(FM$PE$<name>).byteSize();

          private MemorySegment FM$MS$<name>;

          public MemorySegment <name>$MemorySegment() {
            if (FM$MS$<name> == null) {
              FM$MS$<name> = ms.asSlice(FM$OFFSET$<name>, FM$SIZE$<name>);
            }
            return FM$MS$<name>;
          }

          private java.nio.<Type>Buffer FM$BB$<name>;

          public java.nio.<Type>Buffer <name>() {
            if (FM$BB$<name> == null) {
              FM$BB$<name> = <name>$MemorySegment().asByteBuffer()<buffer>;
            }
            return FM$BB$<name>;
          }

          /** get element at offset */
          public <type> <name>(int index) {
            return <name>$MemorySegment()
              .getAtIndex(ValueLayout.JAVA_<TYPE>, index);
          }

          /** set element at offset */
          public void <name>(int index, <type> value) {
            <name>$MemorySegment()
              .setAtIndex(ValueLayout.JAVA_<TYPE>, index, value);
          }

          /** replace values from array */
          public void <name>(<type>[] value) {
            if (value.length != <size>) {
              throw new IllegalArgumentException(); // TODO message
            }

            MemorySegment.copy(value, 0,
                <name>$MemorySegment(), ValueLayout.JAVA_<TYPE>, 0, <size>);
          }
        """
        .replace("<name>", name)
        .replace("<type>", type)
        .replace("<Type>", capType)
        .replace("<TYPE>", type.toUpperCase(Locale.ROOT))
        .replace("<buffer>", type.equals("byte")
            ? "" : "as" + capType + "Buffer()")
        .replace("<size>", Long.toString(size)));
  }

  private String capitalize(String name) {
    return Character.toTitleCase(name.charAt(0)) + name.substring(1);
  }
}

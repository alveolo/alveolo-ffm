package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.RELEASE_21;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("org.alveolo.ffm.ForeignValue")
@SupportedSourceVersion(RELEASE_21)
public class ForeignValueProcessor extends AbstractProcessor {
  @Override
  public boolean process(
      Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {
    var messager = processingEnv.getMessager();

    messager.printNote("ValueProcessor process...");
    messager.printNote("RoundEnvironment"
        + ": processingOver = " + roundEnv.processingOver()
        + ", rootElements = " + roundEnv.getRootElements());

    if (roundEnv.processingOver()) return true;

    for (var annotation : annotations) {
      messager.printNote("Annotation: " + annotation);

      var values = roundEnv.getElementsAnnotatedWith(annotation);

      for (var value : values) {
        messager.printNote("Annotated Element: " + value);

        if (value instanceof TypeElement type) {
          try {
            writeFile(type);
          } catch (Throwable e) {
            messager.printError(e.getMessage(), value);
          }
        }
      }
    }

    return true;
  }

  private void writeFile(TypeElement type) throws IOException {
    if (type.getKind() != ElementKind.RECORD)
      throw new IllegalArgumentException("@Value is not allowed here");

    String srcClassName = type.getQualifiedName().toString();
    String packageName = null;
    int lastDot = srcClassName.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = srcClassName.substring(0, lastDot);
    }
    String className = foreignClassName(type);
    String simpleClassName = className.substring(lastDot + 1);

    var file = processingEnv.getFiler().createSourceFile(className, type);

    try (var out = file.openWriter()) {
      if (packageName != null) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
          import java.lang.foreign.*;

          @javax.annotation.processing.Generated("<generator>")
          public final class <name> {
            public static final MemoryLayout FM$LAYOUT =
              MemoryLayout.structLayout(
                org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<name>", simpleClassName));

      var rcGens = type.getRecordComponents().stream()
          .map(rc -> new VariableGenerator(processingEnv, rc))
          .toList();

      var layoutGen = new MemoryLayoutGenerator(processingEnv, rcGens);

      out.write(layoutGen.layout());

      String toMemorySegmentFields = rcGens.stream()
          .map(VariableGenerator::name)
          .map(name -> "<name>(ms, from.<name>());".replace("<name>", name))
          .collect(joining("\n    ", "", "  "));

      String fromMemorySegmentFields = rcGens.stream()
          .map(VariableGenerator::name)
          .map(name -> "<name>(ms)".replace("<name>", name))
          .collect(joining(",\n        ", "\n        ", ""));

      out.write("""
                }));

            public static MemorySegment allocate(SegmentAllocator allocator) {
              return allocator.allocate(
                FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
            }

            public static void toMemorySegment(<src> from, MemorySegment ms) {
              <toMemorySegmentFields>
            }

            public static MemorySegment toMemorySegment(
                SegmentAllocator allocator, <src> from) {
              var ms = allocate(allocator);
              toMemorySegment(from, ms);
              return ms;
            }

            public static <src> fromMemorySegment(MemorySegment ms) {
              return new <src>(<fromMemorySegmentFields>);
            }
          """
          .replace("<src>", srcClassName)
          .replace("<toMemorySegmentFields>", toMemorySegmentFields)
          .replace("<fromMemorySegmentFields>", fromMemorySegmentFields));

      for (var e : rcGens) {
        writeAccessors(out, e);
      }

      out.write("}\n");
    }
  }

  private void writeAccessors(Writer out, VariableGenerator componentGen)
      throws IOException {
    var name = componentGen.name();

    out.write("""

          public static final MemoryLayout.PathElement FM$PE$<name> =
            MemoryLayout.PathElement.groupElement("<name>");

        """
        .replace("<name>", name)); // TODO override name with annotation

    var type = componentGen.typeName();

    switch (type) {
      case "java.nio.ByteBuffer" -> writeAccessorsBuffer(
          out, name, "byte", componentGen.sequence);
      case "java.nio.CharBuffer" -> writeAccessorsBuffer(
          out, name, "char", componentGen.sequence);
      case "java.nio.ShortBuffer" -> writeAccessorsBuffer(
          out, name, "short", componentGen.sequence);
      case "java.nio.IntBuffer" -> writeAccessorsBuffer(
          out, name, "int", componentGen.sequence);
      case "java.nio.LongBuffer" -> writeAccessorsBuffer(
          out, name, "long", componentGen.sequence);
      case "java.nio.FloatBuffer" -> writeAccessorsBuffer(
          out, name, "float", componentGen.sequence);
      case "java.nio.DoubleBuffer" -> writeAccessorsBuffer(
          out, name, "double", componentGen.sequence);

      default -> writeAccessorsSimple(out, type, name);
    }
  }

  private void writeAccessorsSimple(Writer out, String type, String name)
      throws IOException {
    out.write("""
          public static final java.lang.invoke.VarHandle FM$VH$<name> =
            FM$LAYOUT.varHandle(FM$PE$<name>);

          public static <type> <name>(MemorySegment ms) {
            return (<type>) FM$VH$<name>.get(ms);
          }

          public static void <name>(MemorySegment ms, <type> value) {
            FM$VH$<name>.set(ms, value);
          }
        """
        .replace("<name>", name) // TODO override name with annotation
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
          public static <type> <name>(MemorySegment ms, int index) {
            return <name>$MemorySegment()
              .getAtIndex(ValueLayout.JAVA_<TYPE>, index);
          }

          /** set element at offset */
          public static void <name>(MemorySegment ms, int index, <type> value) {
            <name>$MemorySegment()
              .setAtIndex(ValueLayout.JAVA_<TYPE>, index, value);
          }

          /** replace values from array */
          public static void <name>(MemorySegment ms, <type>[] value) {
            if (value.length != <size>) {
              throw new IllegalArgumentException(); // TODO message
            }

            MemorySegment.copy(value, 0,
                <name>$MemorySegment(), ValueLayout.JAVA_<TYPE>, 0, <size>);
          }
        """
        .replace("<name>", name) // TODO override name with annotation
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

package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ForeignValueProcessorTest extends AbstractProcessorTest {

  @Test
  void generatesRecordWithPrimitivesFM() {
    var source = forSourceString("test.Point", """
        package test;
        import org.alveolo.ffm.ForeignValue;
        @ForeignValue
        public record Point(int x, int y) {}
        """);

    var expected = forSourceString("test.PointFM", """
        package test;
        import java.lang.foreign.*;
        import java.lang.invoke.*;
        @javax.annotation.processing.Generated(
            "org.alveolo.ffm.processor.ForeignValueProcessor")
        public final class PointFM {
          public static final MemoryLayout FM$LAYOUT =
            MemoryLayout.structLayout(
              org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
                ValueLayout.JAVA_INT.withName("x"),
                ValueLayout.JAVA_INT.withName("y"),
              }));
          public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(
              FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
          }
          public static void toMemorySegment(
              test.Point from, MemorySegment ms) {
            x(ms, from.x());
            y(ms, from.y());
          }
          public static MemorySegment toMemorySegment(
              SegmentAllocator allocator, test.Point from) {
            var ms = allocate(allocator);
            toMemorySegment(from, ms);
            return ms;
          }
          public static test.Point fromMemorySegment(MemorySegment ms) {
            return new test.Point(
                x(ms),
                y(ms));
          }

          public static final MemoryLayout.PathElement FM$PE$x =
            MemoryLayout.PathElement.groupElement("x");
          public static final java.lang.invoke.VarHandle FM$VH$x =
            MethodHandles.insertCoordinates(
                FM$LAYOUT.varHandle(FM$PE$x), 1, 0L);
          public static int x(MemorySegment ms) {
            return (int) FM$VH$x.get(ms);
          }
          public static void x(MemorySegment ms, int value) {
            FM$VH$x.set(ms, value);
          }

          public static final MemoryLayout.PathElement FM$PE$y =
            MemoryLayout.PathElement.groupElement("y");
          public static final java.lang.invoke.VarHandle FM$VH$y =
            MethodHandles.insertCoordinates(
                FM$LAYOUT.varHandle(FM$PE$y), 1, 0L);
          public static int y(MemorySegment ms) {
            return (int) FM$VH$y.get(ms);
          }
          public static void y(MemorySegment ms, int value) {
            FM$VH$y.set(ms, value);
          }
        }
        """);

    var compilation = compile(source);
    assertThat(compilation).succeeded();
    assertThat(compilation).generatedSourceFile("test.PointFM")
        .hasSourceEquivalentTo(expected);
  }

  @Disabled
  @Test
  void generatesRecordWithByteBufferFM() {
    var source = forSourceString("test.BufferData", """
        package test;
        import org.alveolo.ffm.ForeignValue;
        @ForeignValue
        public record BufferData(java.nio.ByteBuffer data) {}
        """);

    var expected = forSourceString("test.BufferDataFM", """
        package test;

        import java.lang.foreign.*;
        import java.lang.invoke.*;

        @javax.annotation.processing.Generated(
            "org.alveolo.ffm.processor.ForeignValueProcessor")
        public final class BufferDataFM {
          public static final MemoryLayout FM$LAYOUT =
            MemoryLayout.structLayout(
              org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
                MemoryLayout.sequenceLayout(1, ValueLayout.JAVA_BYTE)
                    .withName("data"),
              }));

          public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(
              FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
          }

          public static void toMemorySegment(
              test.BufferData from, MemorySegment ms) {
            data(ms, from.data());
          }

          public static MemorySegment toMemorySegment(
              SegmentAllocator allocator, test.BufferData from) {
            var ms = allocate(allocator);
            toMemorySegment(from, ms);
            return ms;
          }

          public static test.BufferData fromMemorySegment(MemorySegment ms) {
            return new test.BufferData(data(ms));
          }

          public static final MemoryLayout.PathElement FM$PE$data =
            MemoryLayout.PathElement.groupElement("data");

          public static final long FM$OFFSET$data =
              FM$LAYOUT.byteOffset(FM$PE$data);

          public static final long FM$SIZE$data =
              FM$LAYOUT.select(FM$PE$data).byteSize();

          private MemorySegment FM$MS$data;

          public MemorySegment data$MemorySegment() {
            if (FM$MS$data == null) {
              FM$MS$data = ms.asSlice(FM$OFFSET$data, FM$SIZE$data);
            }
            return FM$MS$data;
          }

          private java.nio.ByteBuffer FM$BB$data;

          public java.nio.ByteBuffer data() {
            if (FM$BB$data == null) {
              FM$BB$data = data$MemorySegment().asByteBuffer();
            }
            return FM$BB$data;
          }

          /** get element at offset */
          public static byte data(MemorySegment ms, int index) {
            return data$MemorySegment()
              .getAtIndex(ValueLayout.JAVA_BYTE, index);
          }

          /** set element at offset */
          public static void data(MemorySegment ms, int index, byte value) {
            data$MemorySegment()
              .setAtIndex(ValueLayout.JAVA_BYTE, index, value);
          }

          /** replace values from array */
          public static void data(MemorySegment ms, byte[] value) {
            if (value.length != 1) {
              throw new IllegalArgumentException(); // TODO message
            }

            MemorySegment.copy(value, 0,
                data$MemorySegment(), ValueLayout.JAVA_BYTE, 0, 1);
          }
        }
        """);

    var compilation = compile(source);
    assertThat(compilation).succeeded();
    assertThat(compilation).generatedSourceFile("test.BufferDataFM")
        .hasSourceEquivalentTo(expected);
  }

  @Disabled
  @Test
  void generatesRecordWithIntBufferFM() {
    var source = forSourceString("test.IntData", """
        package test;
        import org.alveolo.ffm.ForeignValue;
        @ForeignValue
        public record IntData(java.nio.IntBuffer data) {}
        """);

    var expected = forSourceString("test.IntDataFM", """
        package test;

        import java.lang.foreign.*;
        import java.lang.invoke.*;

        @javax.annotation.processing.Generated(
            "org.alveolo.ffm.processor.ForeignValueProcessor")
        public final class IntDataFM {
          public static final MemoryLayout FM$LAYOUT =
            MemoryLayout.structLayout(
              org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
                MemoryLayout.sequenceLayout(1, ValueLayout.JAVA_INT)
                    .withName("data"),
              }));

          public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(
              FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
          }

          public static void toMemorySegment(
              test.IntData from, MemorySegment ms) {
            data(ms, from.data());
          }

          public static MemorySegment toMemorySegment(
              SegmentAllocator allocator, test.IntData from) {
            var ms = allocate(allocator);
            toMemorySegment(from, ms);
            return ms;
          }

          public static test.IntData fromMemorySegment(MemorySegment ms) {
            return new test.IntData(
                data(ms));
          }

          public static final MemoryLayout.PathElement FM$PE$data =
            MemoryLayout.PathElement.groupElement("data");

          public static final long FM$OFFSET$data =
              FM$LAYOUT.byteOffset(FM$PE$data);

          public static final long FM$SIZE$data =
              FM$LAYOUT.select(FM$PE$data).byteSize();

          private MemorySegment FM$MS$data;

          public MemorySegment data$MemorySegment() {
            if (FM$MS$data == null) {
              FM$MS$data = ms.asSlice(FM$OFFSET$data, FM$SIZE$data);
            }
            return FM$MS$data;
          }

          private java.nio.IntBuffer FM$BB$data;

          public java.nio.IntBuffer data() {
            if (FM$BB$data == null) {
              FM$BB$data = data$MemorySegment().asByteBuffer().asIntBuffer();
            }
            return FM$BB$data;
          }

          /** get element at offset */
          public static int data(MemorySegment ms, int index) {
            return data$MemorySegment()
              .getAtIndex(ValueLayout.JAVA_INT, index);
          }

          /** set element at offset */
          public static void data(MemorySegment ms, int index, int value) {
            data$MemorySegment()
              .setAtIndex(ValueLayout.JAVA_INT, index, value);
          }

          /** replace values from array */
          public static void data(MemorySegment ms, int[] value) {
            if (value.length != 1) {
              throw new IllegalArgumentException(); // TODO message
            }

            MemorySegment.copy(value, 0,
                data$MemorySegment(), ValueLayout.JAVA_INT, 0, 1);
          }
        }
        """);

    var compilation = compile(source);
    assertThat(compilation).succeeded();
    assertThat(compilation).generatedSourceFile("test.IntDataFM")
        .hasSourceEquivalentTo(expected);
  }

  @Disabled
  @Test
  void generatesRecordWithMixedTypesRM() {
    var source = forSourceString("test.Mixed", """
        package test;
        import org.alveolo.ffm.ForeignValue;
        @ForeignValue
        public record Mixed(int id, String name) {}
        """);
    var compilation = compile(source);
    assertThat(compilation).succeeded();

    String generated = getGeneratedSource(compilation, "test.MixedFM");
    assertContains(generated, "ValueLayout.JAVA_INT.withName(\"id\")");
    assertContains(generated, "ValueLayout.ADDRESS.withName(\"name\")");
    assertContains(generated, "ff$arena.allocateFrom(name)");
  }

  @Test
  void generatesRecordWithAddressTypeFM() {
    var source = forSourceString("test.Ptr", """
        package test;
        import org.alveolo.ffm.ForeignValue;
        import org.alveolo.ffm.Address;
        @ForeignValue
        public record Ptr(@Address Object ptr) {}
        """);
    var compilation = compile(source);
    assertThat(compilation).succeeded();

    String generated = getGeneratedSource(compilation, "test.PtrFM");
    assertContains(generated, "ValueLayout.ADDRESS.withName(\"ptr\")");
  }

  @Test
  void failsOnNonRecordClass() {
    var source = forSourceString("test.BadClass", """
        package test;
        import org.alveolo.ffm.ForeignValue;
        @ForeignValue
        public class BadClass {
          int x;
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("@ForeignValue is not allowed here");
  }

  @Test
  void failsOnUnsupportedType() {
    var source = forSourceString("test.BadRecord", """
        package test;
        import org.alveolo.ffm.ForeignValue;
        import java.util.List;
        @ForeignValue
        public record BadRecord(List<String> data) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("Type is not supported: java.util.List");
  }
}

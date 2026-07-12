package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ArraySnapshotFM {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        MemoryLayout.sequenceLayout(4L,
            ValueLayout.JAVA_BYTE).withName("bytes"),
        MemoryLayout.sequenceLayout(2L,
            pkg.ArrayPointFM.FM$LAYOUT).withName("points"),
      }));

  public static final MemoryLayout.PathElement FM$PE$bytes =
      MemoryLayout.PathElement.groupElement("bytes");

  public static final MemoryLayout.PathElement FM$PE$bytes$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$bytes =
      FM$LAYOUT.select(FM$PE$bytes);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$bytes =
      ValueLayout.JAVA_BYTE;

  public static final long FM$OFFSET$bytes =
      FM$LAYOUT.byteOffset(FM$PE$bytes);

  public static final long FM$SIZE$bytes =
      FM$LAYOUT$bytes.byteSize();

  public static final long FM$DIMENSION$bytes$0 = 4L;

  public static final MemoryLayout.PathElement FM$PE$points =
      MemoryLayout.PathElement.groupElement("points");

  public static final MemoryLayout.PathElement FM$PE$points$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$points =
      FM$LAYOUT.select(FM$PE$points);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$points =
      pkg.ArrayPointFM.FM$LAYOUT;

  public static final long FM$OFFSET$points =
      FM$LAYOUT.byteOffset(FM$PE$points);

  public static final long FM$SIZE$points =
      FM$LAYOUT$points.byteSize();

  public static final long FM$DIMENSION$points$0 = 2L;

  public static final java.lang.invoke.VarHandle FM$VH$bytes =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$bytes, FM$PE$bytes$0), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static MemorySegment allocate(
      SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static ArraySnapshot reinterpret(MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static MemorySegment reinterpret(
      MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static MemorySegment FM$at(MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static ArraySnapshot at(MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(ArraySnapshot from, MemorySegment ms) {
    bytes(ms, from.bytes());
    points(ms, from.points());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, ArraySnapshot from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms);
    return ms;
  }

  public static ArraySnapshot fromMemorySegment(MemorySegment ms) {
    return new ArraySnapshot(
        bytes(ms),
        points(ms));
  }

  public static MemorySegment bytes$MemorySegment(MemorySegment ms) {
    return ms.asSlice(FM$OFFSET$bytes, FM$SIZE$bytes);
  }

  public static MemorySegment bytes$MemorySegment(
      MemorySegment ms, long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$bytes,
            MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$bytes.byteSize());
  }

  public static byte bytes(
      MemorySegment ms, long index) {
    return (byte) FM$VH$bytes.get(ms, index);
  }

  public static void bytes(MemorySegment ms, long index,
      byte value) {
    FM$VH$bytes.set(ms, index, value);
  }

  public static java.nio.ByteBuffer bytes$Buffer(MemorySegment ms) {
    return bytes$MemorySegment(ms).asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder());
  }

  public static byte[] bytes$Array(MemorySegment ms) {
    var value = new byte[(int) FM$DIMENSION$bytes$0];
    for (long index = 0; index < value.length; index++) {
      value[(int) index] = bytes(ms, index);
    }
    return value;
  }

  public static byte[] bytes(MemorySegment ms) {
    return bytes$Array(ms);
  }

  public static void bytes(MemorySegment ms, byte[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != FM$DIMENSION$bytes$0) {
      throw new IllegalArgumentException(
          "bytes length must be " + FM$DIMENSION$bytes$0);
    }
    for (long index = 0; index < value.length; index++) {
      bytes(ms, index, value[(int) index]);
    }
  }

  public static MemorySegment points$MemorySegment(MemorySegment ms) {
    return ms.asSlice(FM$OFFSET$points, FM$SIZE$points);
  }

  public static MemorySegment points$MemorySegment(
      MemorySegment ms, long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$points,
            MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$points.byteSize());
  }

  public static pkg.ArrayPoint points(
      MemorySegment ms, long index) {
    return pkg.ArrayPointFM.fromMemorySegment(
        points$MemorySegment(ms, index));
  }

  public static void points(MemorySegment ms, long index,
      pkg.ArrayPoint value) {
    pkg.ArrayPointFM.toMemorySegment(
        value, points$MemorySegment(ms, index));
  }

  public static pkg.ArrayPoint[] points$Array(MemorySegment ms) {
    var value = new pkg.ArrayPoint[(int) FM$DIMENSION$points$0];
    for (long index = 0; index < value.length; index++) {
      value[(int) index] = points(ms, index);
    }
    return value;
  }

  public static pkg.ArrayPoint[] points(MemorySegment ms) {
    return points$Array(ms);
  }

  public static void points(MemorySegment ms,
      pkg.ArrayPoint[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != FM$DIMENSION$points$0) {
      throw new IllegalArgumentException(
          "points length must be " + FM$DIMENSION$points$0);
    }
    for (long index = 0; index < value.length; index++) {
      points(ms, index, value[(int) index]);
    }
  }
}

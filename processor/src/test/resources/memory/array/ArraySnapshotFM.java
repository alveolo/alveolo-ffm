package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ArraySnapshotFM {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.MemoryLayout.sequenceLayout(4L,
            java.lang.foreign.ValueLayout.JAVA_BYTE).withName("bytes"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            pkg.ArrayPointFM.FM$LAYOUT).withName("points"),
      }));

  public static java.lang.foreign.MemorySegment allocate(java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static ArraySnapshot reinterpret(java.lang.foreign.MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret(
      java.lang.foreign.MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment FM$at(java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static ArraySnapshot at(java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(ArraySnapshot from, java.lang.foreign.MemorySegment ms) {
    bytes(ms, from.bytes());
    points(ms, from.points());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment(
      java.lang.foreign.SegmentAllocator allocator, ArraySnapshot from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms);
    return ms;
  }

  public static ArraySnapshot fromMemorySegment(java.lang.foreign.MemorySegment ms) {
    return new ArraySnapshot(
        bytes(ms),
        points(ms));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$bytes =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("bytes");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$bytes$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$bytes =
      FM$LAYOUT.select(FM$PE$bytes);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$bytes =
      java.lang.foreign.ValueLayout.JAVA_BYTE;

  public static final long FM$OFFSET$bytes =
      FM$LAYOUT.byteOffset(FM$PE$bytes);

  public static final long FM$SIZE$bytes =
      FM$LAYOUT$bytes.byteSize();

  public static final long FM$DIMENSION$bytes$0 = 4L;

  public static final java.lang.invoke.VarHandle FM$VH$bytes =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$bytes, FM$PE$bytes$0), 1, 0L);

  public static java.lang.foreign.MemorySegment bytes$MemorySegment(java.lang.foreign.MemorySegment ms) {
    return ms.asSlice(FM$OFFSET$bytes, FM$SIZE$bytes);
  }

  public static java.lang.foreign.MemorySegment bytes$MemorySegment(
      java.lang.foreign.MemorySegment ms, long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$bytes,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$bytes.byteSize());
  }

  public static byte bytes(
      java.lang.foreign.MemorySegment ms, long index) {
    return (byte) FM$VH$bytes.get(ms, index);
  }

  public static void bytes(java.lang.foreign.MemorySegment ms, long index,
      byte value) {
    FM$VH$bytes.set(ms, index, value);
  }

  public static java.nio.ByteBuffer bytes$Buffer(java.lang.foreign.MemorySegment ms) {
    return bytes$MemorySegment(ms).asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder());
  }

  public static byte[] bytes$Array(java.lang.foreign.MemorySegment ms) {
    var value = new byte[(int) FM$DIMENSION$bytes$0];
    for (long index = 0; index < value.length; index++) {
      value[(int) index] = bytes(ms, index);
    }
    return value;
  }

  public static byte[] bytes(java.lang.foreign.MemorySegment ms) {
    return bytes$Array(ms);
  }

  public static void bytes(java.lang.foreign.MemorySegment ms, byte[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != FM$DIMENSION$bytes$0) {
      throw new IllegalArgumentException(
          "bytes length must be " + FM$DIMENSION$bytes$0);
    }
    for (long index = 0; index < value.length; index++) {
      bytes(ms, index, value[(int) index]);
    }
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$points =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("points");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$points$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$points =
      FM$LAYOUT.select(FM$PE$points);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$points =
      pkg.ArrayPointFM.FM$LAYOUT;

  public static final long FM$OFFSET$points =
      FM$LAYOUT.byteOffset(FM$PE$points);

  public static final long FM$SIZE$points =
      FM$LAYOUT$points.byteSize();

  public static final long FM$DIMENSION$points$0 = 2L;

  public static java.lang.foreign.MemorySegment points$MemorySegment(java.lang.foreign.MemorySegment ms) {
    return ms.asSlice(FM$OFFSET$points, FM$SIZE$points);
  }

  public static java.lang.foreign.MemorySegment points$MemorySegment(
      java.lang.foreign.MemorySegment ms, long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$points,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$points.byteSize());
  }

  public static pkg.ArrayPoint points(
      java.lang.foreign.MemorySegment ms, long index) {
    return pkg.ArrayPointFM.fromMemorySegment(
        points$MemorySegment(ms, index));
  }

  public static void points(java.lang.foreign.MemorySegment ms, long index,
      pkg.ArrayPoint value) {
    pkg.ArrayPointFM.toMemorySegment(
        value, points$MemorySegment(ms, index));
  }

  public static pkg.ArrayPoint[] points$Array(java.lang.foreign.MemorySegment ms) {
    var value = new pkg.ArrayPoint[(int) FM$DIMENSION$points$0];
    for (long index = 0; index < value.length; index++) {
      value[(int) index] = points(ms, index);
    }
    return value;
  }

  public static pkg.ArrayPoint[] points(java.lang.foreign.MemorySegment ms) {
    return points$Array(ms);
  }

  public static void points(java.lang.foreign.MemorySegment ms,
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

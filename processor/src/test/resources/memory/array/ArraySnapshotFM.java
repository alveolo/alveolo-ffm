package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ArraySnapshotFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.MemoryLayout.sequenceLayout(4L,
            java.lang.foreign.ValueLayout.JAVA_BYTE).withName("bytes"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            pkg.ArrayPointFM.MemoryLayout$F).withName("points"),
      }));

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(MemoryLayout$F, count);
  }

  public static ArraySnapshot reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return fromMemorySegment$F(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return memorySegment.reinterpret(Math.multiplyExact(
        MemoryLayout$F.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment elementAt$F(
      java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, MemoryLayout$F.byteSize()), MemoryLayout$F.byteSize());
  }

  public static ArraySnapshot at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      ArraySnapshot source,
      java.lang.foreign.MemorySegment memorySegment) {
    bytes(memorySegment, source.bytes());
    points(memorySegment, source.points());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      ArraySnapshot source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment);
    return memorySegment;
  }

  public static ArraySnapshot fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new ArraySnapshot(
        bytes(memorySegment),
        points(memorySegment));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      bytes$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("bytes");

  public static final java.lang.foreign.MemoryLayout.PathElement
      bytes$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      bytes$MemoryLayout$F =
          MemoryLayout$F.select(bytes$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      bytes$ElementMemoryLayout$F =
      java.lang.foreign.ValueLayout.JAVA_BYTE;

  public static final long bytes$Sequence0Dimension$F =
      4L;

  public static final java.lang.invoke.VarHandle bytes$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(
              bytes$PathElement$F, bytes$Sequence0PathElement$F), 1, 0L);

  public static java.lang.foreign.MemorySegment
      bytesAsMemorySegment$F(
          java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.asSlice(
        MemoryLayout$F.byteOffset(bytes$PathElement$F),
        bytes$MemoryLayout$F.byteSize());
  }

  public static java.lang.foreign.MemorySegment
      bytesAsMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment, long index0) {
    return memorySegment.asSlice(
        MemoryLayout$F.byteOffset(
            bytes$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        bytes$ElementMemoryLayout$F.byteSize());
  }

  public static byte bytes(
      java.lang.foreign.MemorySegment memorySegment, long index0$f) {
    return (byte) bytes$VarHandle$F.get(memorySegment, index0$f);
  }

  public static void bytes(
      java.lang.foreign.MemorySegment memorySegment,
      long index0$f,
      byte value) {
    bytes$VarHandle$F.set(memorySegment, index0$f, value);
  }

  public static java.nio.ByteBuffer bytesAsBuffer$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return bytesAsMemorySegment$F(memorySegment).asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder());
  }

  public static byte[] bytesToArray$F(
      java.lang.foreign.MemorySegment memorySegment) {
    var result =
        new byte[(int) bytes$Sequence0Dimension$F];
    for (long index = 0;
        index < result.length; index++) {
      result[(int) index] =
          bytes(memorySegment, index);
    }
    return result;
  }

  public static byte[] bytes(
      java.lang.foreign.MemorySegment memorySegment) {
    return bytesToArray$F(memorySegment);
  }

  public static void bytes(
      java.lang.foreign.MemorySegment memorySegment,
      byte[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != bytes$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "bytes length must be "
              + bytes$Sequence0Dimension$F);
    }
    for (long index = 0;
        index < value.length; index++) {
      bytes(
          memorySegment, index, value[(int) index]);
    }
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      points$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("points");

  public static final java.lang.foreign.MemoryLayout.PathElement
      points$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      points$MemoryLayout$F =
          MemoryLayout$F.select(points$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      points$ElementMemoryLayout$F =
      pkg.ArrayPointFM.MemoryLayout$F;

  public static final long points$Sequence0Dimension$F =
      2L;

  public static java.lang.foreign.MemorySegment
      pointsAsMemorySegment$F(
          java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.asSlice(
        MemoryLayout$F.byteOffset(points$PathElement$F),
        points$MemoryLayout$F.byteSize());
  }

  public static java.lang.foreign.MemorySegment
      pointsAsMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment, long index0) {
    return memorySegment.asSlice(
        MemoryLayout$F.byteOffset(
            points$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        points$ElementMemoryLayout$F.byteSize());
  }

  public static pkg.ArrayPoint points(
      java.lang.foreign.MemorySegment memorySegment, long index0$f) {
    return pkg.ArrayPointFM.fromMemorySegment$F(
        pointsAsMemorySegment$F(memorySegment, index0$f));
  }

  public static void points(
      java.lang.foreign.MemorySegment memorySegment,
      long index0$f,
      pkg.ArrayPoint value) {
    pkg.ArrayPointFM.toMemorySegment$F(
        value, pointsAsMemorySegment$F(memorySegment, index0$f));
  }

  public static pkg.ArrayPoint[] pointsToArray$F(
      java.lang.foreign.MemorySegment memorySegment) {
    var result =
        new pkg.ArrayPoint[(int) points$Sequence0Dimension$F];
    for (long index = 0;
        index < result.length; index++) {
      result[(int) index] =
          points(memorySegment, index);
    }
    return result;
  }

  public static pkg.ArrayPoint[] points(
      java.lang.foreign.MemorySegment memorySegment) {
    return pointsToArray$F(memorySegment);
  }

  public static void points(
      java.lang.foreign.MemorySegment memorySegment,
      pkg.ArrayPoint[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != points$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "points length must be "
              + points$Sequence0Dimension$F);
    }
    for (long index = 0;
        index < value.length; index++) {
      points(memorySegment, index,
          value[(int) index]);
    }
  }
}

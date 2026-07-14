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
      java.lang.foreign.SegmentAllocator allocator$f) {
    return allocator$f.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator$f, long count$f) {
    if (count$f < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator$f.allocate(MemoryLayout$F, count$f);
  }

  public static ArraySnapshot reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return fromMemorySegment$F(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f, long count$f) {
    if (count$f < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return memorySegment$f.reinterpret(Math.multiplyExact(
        MemoryLayout$F.byteSize(), count$f));
  }

  private static java.lang.foreign.MemorySegment elementAt$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    if (index$f < 0) {
      throw new IndexOutOfBoundsException(index$f);
    }
    return array$f.asSlice(Math.multiplyExact(
        index$f, MemoryLayout$F.byteSize()), MemoryLayout$F.byteSize());
  }

  public static ArraySnapshot at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return fromMemorySegment$F(elementAt$F(array$f, index$f));
  }

  public static void toMemorySegment$F(
      ArraySnapshot source$f,
      java.lang.foreign.MemorySegment memorySegment$f) {
    bytes(memorySegment$f, source$f.bytes());
    points(memorySegment$f, source$f.points());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator$f,
      ArraySnapshot source$f) {
    var memorySegment$f = allocate$F(allocator$f);
    toMemorySegment$F(source$f, memorySegment$f);
    return memorySegment$f;
  }

  public static ArraySnapshot fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new ArraySnapshot(
        bytes(memorySegment$f),
        points(memorySegment$f));
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

  public static final long bytes$Sequence0Dimension$F = 4L;

  public static final java.lang.invoke.VarHandle bytes$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(
              bytes$PathElement$F, bytes$Sequence0PathElement$F), 1, 0L);

  public static java.lang.foreign.MemorySegment
      bytesAsMemorySegment$F(
          java.lang.foreign.MemorySegment memorySegment$f) {
    return memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(bytes$PathElement$F),
        bytes$MemoryLayout$F.byteSize());
  }

  public static java.lang.foreign.MemorySegment
      bytesAsMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f, long index$f) {
    return memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(
            bytes$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        bytes$ElementMemoryLayout$F.byteSize());
  }

  public static byte bytes(
      java.lang.foreign.MemorySegment memorySegment$f, long index$f) {
    return (byte) bytes$VarHandle$F.get(memorySegment$f, index$f);
  }

  public static void bytes(
      java.lang.foreign.MemorySegment memorySegment$f,
      long index$f,
      byte value$f) {
    bytes$VarHandle$F.set(memorySegment$f, index$f, value$f);
  }

  public static java.nio.ByteBuffer bytesAsBuffer$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return bytesAsMemorySegment$F(memorySegment$f).asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder());
  }

  public static byte[] bytesToArray$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    var result$f =
        new byte[(int) bytes$Sequence0Dimension$F];
    for (long index$f = 0;
        index$f < result$f.length; index$f++) {
      result$f[(int) index$f] =
          bytes(memorySegment$f, index$f);
    }
    return result$f;
  }

  public static byte[] bytes(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return bytesToArray$F(memorySegment$f);
  }

  public static void bytes(
      java.lang.foreign.MemorySegment memorySegment$f,
      byte[] value$f) {
    java.util.Objects.requireNonNull(value$f, "value");
    if (value$f.length != bytes$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "bytes length must be " + bytes$Sequence0Dimension$F);
    }
    for (long index$f = 0;
        index$f < value$f.length; index$f++) {
      bytes(
          memorySegment$f, index$f, value$f[(int) index$f]);
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

  public static final long points$Sequence0Dimension$F = 2L;

  public static java.lang.foreign.MemorySegment
      pointsAsMemorySegment$F(
          java.lang.foreign.MemorySegment memorySegment$f) {
    return memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(points$PathElement$F),
        points$MemoryLayout$F.byteSize());
  }

  public static java.lang.foreign.MemorySegment
      pointsAsMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f, long index$f) {
    return memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(
            points$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        points$ElementMemoryLayout$F.byteSize());
  }

  public static pkg.ArrayPoint points(
      java.lang.foreign.MemorySegment memorySegment$f, long index$f) {
    return pkg.ArrayPointFM.fromMemorySegment$F(
        pointsAsMemorySegment$F(memorySegment$f, index$f));
  }

  public static void points(
      java.lang.foreign.MemorySegment memorySegment$f,
      long index$f,
      pkg.ArrayPoint value$f) {
    pkg.ArrayPointFM.toMemorySegment$F(
        value$f, pointsAsMemorySegment$F(memorySegment$f, index$f));
  }

  public static pkg.ArrayPoint[] pointsToArray$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    var result$f =
        new pkg.ArrayPoint[(int) points$Sequence0Dimension$F];
    for (long index$f = 0;
        index$f < result$f.length; index$f++) {
      result$f[(int) index$f] =
          points(memorySegment$f, index$f);
    }
    return result$f;
  }

  public static pkg.ArrayPoint[] points(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return pointsToArray$F(memorySegment$f);
  }

  public static void points(
      java.lang.foreign.MemorySegment memorySegment$f,
      pkg.ArrayPoint[] value$f) {
    java.util.Objects.requireNonNull(value$f, "value");
    if (value$f.length != points$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "points length must be " + points$Sequence0Dimension$F);
    }
    for (long index$f = 0;
        index$f < value$f.length; index$f++) {
      points(memorySegment$f, index$f,
          value$f[(int) index$f]);
    }
  }
}

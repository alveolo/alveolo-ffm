package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class AllocatingArraySnapshotFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            pkg.ArrayAddressValueFM.MemoryLayout$F).withName("values"),
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

  public static AllocatingArraySnapshot reinterpret$F(
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

  public static AllocatingArraySnapshot at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return fromMemorySegment$F(elementAt$F(array$f, index$f));
  }

  public static void toMemorySegment$F(
      AllocatingArraySnapshot source$f,
      java.lang.foreign.MemorySegment memorySegment$f,
      java.lang.foreign.SegmentAllocator allocator$f) {
    values(memorySegment$f, allocator$f, source$f.values());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator$f,
      AllocatingArraySnapshot source$f) {
    var memorySegment$f = allocate$F(allocator$f);
    toMemorySegment$F(source$f, memorySegment$f, allocator$f);
    return memorySegment$f;
  }

  public static AllocatingArraySnapshot fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new AllocatingArraySnapshot(
        values(memorySegment$f));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      values$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("values");

  public static final java.lang.foreign.MemoryLayout.PathElement
      values$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      values$MemoryLayout$F =
          MemoryLayout$F.select(values$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      values$ElementMemoryLayout$F =
      pkg.ArrayAddressValueFM.MemoryLayout$F;

  public static final long values$Sequence0Dimension$F = 2L;

  public static java.lang.foreign.MemorySegment
      valuesAsMemorySegment$F(
          java.lang.foreign.MemorySegment memorySegment$f) {
    return memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(values$PathElement$F),
        values$MemoryLayout$F.byteSize());
  }

  public static java.lang.foreign.MemorySegment
      valuesAsMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f, long index$f) {
    return memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(
            values$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        values$ElementMemoryLayout$F.byteSize());
  }

  public static pkg.ArrayAddressValue values(
      java.lang.foreign.MemorySegment memorySegment$f, long index$f) {
    return pkg.ArrayAddressValueFM.fromMemorySegment$F(
        valuesAsMemorySegment$F(memorySegment$f, index$f));
  }

  public static void values(
      java.lang.foreign.MemorySegment memorySegment$f,
      java.lang.foreign.SegmentAllocator allocator$f, long index$f,
      pkg.ArrayAddressValue value$f) {
    pkg.ArrayAddressValueFM.toMemorySegment$F(
        value$f, valuesAsMemorySegment$F(memorySegment$f, index$f), allocator$f);
  }

  public static pkg.ArrayAddressValue[] valuesToArray$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    var result$f =
        new pkg.ArrayAddressValue[(int) values$Sequence0Dimension$F];
    for (long index$f = 0;
        index$f < result$f.length; index$f++) {
      result$f[(int) index$f] =
          values(memorySegment$f, index$f);
    }
    return result$f;
  }

  public static pkg.ArrayAddressValue[] values(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return valuesToArray$F(memorySegment$f);
  }

  public static void values(
      java.lang.foreign.MemorySegment memorySegment$f,
      java.lang.foreign.SegmentAllocator allocator$f, pkg.ArrayAddressValue[] value$f) {
    java.util.Objects.requireNonNull(value$f, "value");
    if (value$f.length != values$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "values length must be " + values$Sequence0Dimension$F);
    }
    for (long index$f = 0;
        index$f < value$f.length; index$f++) {
      values(memorySegment$f, allocator$f, index$f,
          value$f[(int) index$f]);
    }
  }
}

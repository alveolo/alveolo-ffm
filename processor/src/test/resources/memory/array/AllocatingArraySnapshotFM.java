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

  public static AllocatingArraySnapshot reinterpret$F(
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

  public static AllocatingArraySnapshot at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      AllocatingArraySnapshot source,
      java.lang.foreign.MemorySegment memorySegment,
      java.lang.foreign.SegmentAllocator allocator) {
    values(memorySegment, allocator, source.values());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      AllocatingArraySnapshot source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment, allocator);
    return memorySegment;
  }

  public static AllocatingArraySnapshot fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new AllocatingArraySnapshot(
        values(memorySegment));
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

  public static final long values$Sequence0Dimension$F =
      2L;

  public static java.lang.foreign.MemorySegment
      valuesAsMemorySegment$F(
          java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.asSlice(
        MemoryLayout$F.byteOffset(values$PathElement$F),
        values$MemoryLayout$F.byteSize());
  }

  public static java.lang.foreign.MemorySegment
      valuesAsMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment, long index0) {
    return memorySegment.asSlice(
        MemoryLayout$F.byteOffset(
            values$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        values$ElementMemoryLayout$F.byteSize());
  }

  public static pkg.ArrayAddressValue values(
      java.lang.foreign.MemorySegment memorySegment, long index0$f) {
    return pkg.ArrayAddressValueFM.fromMemorySegment$F(
        valuesAsMemorySegment$F(memorySegment, index0$f));
  }

  public static void values(
      java.lang.foreign.MemorySegment memorySegment,
      java.lang.foreign.SegmentAllocator allocator, long index0$f,
      pkg.ArrayAddressValue value) {
    pkg.ArrayAddressValueFM.toMemorySegment$F(
        value, valuesAsMemorySegment$F(memorySegment, index0$f), allocator);
  }

  public static pkg.ArrayAddressValue[] valuesToArray$F(
      java.lang.foreign.MemorySegment memorySegment) {
    var result =
        new pkg.ArrayAddressValue[(int) values$Sequence0Dimension$F];
    for (long index = 0;
        index < result.length; index++) {
      result[(int) index] =
          values(memorySegment, index);
    }
    return result;
  }

  public static pkg.ArrayAddressValue[] values(
      java.lang.foreign.MemorySegment memorySegment) {
    return valuesToArray$F(memorySegment);
  }

  public static void values(
      java.lang.foreign.MemorySegment memorySegment,
      java.lang.foreign.SegmentAllocator allocator, pkg.ArrayAddressValue[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != values$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "values length must be "
              + values$Sequence0Dimension$F);
    }
    for (long index = 0;
        index < value.length; index++) {
      values(memorySegment, allocator, index,
          value[(int) index]);
    }
  }
}

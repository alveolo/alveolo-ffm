package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class AllocatingArraySnapshotFM {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            pkg.ArrayAddressValueFM.FM$LAYOUT).withName("values"),
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

  public static AllocatingArraySnapshot reinterpret(java.lang.foreign.MemorySegment ms) {
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

  public static AllocatingArraySnapshot at(java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(
      AllocatingArraySnapshot from, java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator ff$allocator) {
    values(ms, ff$allocator, from.values());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment(
      java.lang.foreign.SegmentAllocator allocator, AllocatingArraySnapshot from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static AllocatingArraySnapshot fromMemorySegment(java.lang.foreign.MemorySegment ms) {
    return new AllocatingArraySnapshot(
        values(ms));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$values =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("values");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$values$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$values =
      FM$LAYOUT.select(FM$PE$values);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$values =
      pkg.ArrayAddressValueFM.FM$LAYOUT;

  public static final long FM$OFFSET$values =
      FM$LAYOUT.byteOffset(FM$PE$values);

  public static final long FM$SIZE$values =
      FM$LAYOUT$values.byteSize();

  public static final long FM$DIMENSION$values$0 = 2L;

  public static java.lang.foreign.MemorySegment values$MemorySegment(java.lang.foreign.MemorySegment ms) {
    return ms.asSlice(FM$OFFSET$values, FM$SIZE$values);
  }

  public static java.lang.foreign.MemorySegment values$MemorySegment(
      java.lang.foreign.MemorySegment ms, long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$values,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$values.byteSize());
  }

  public static pkg.ArrayAddressValue values(
      java.lang.foreign.MemorySegment ms, long index) {
    return pkg.ArrayAddressValueFM.fromMemorySegment(
        values$MemorySegment(ms, index));
  }

  public static void values(java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator allocator, long index,
      pkg.ArrayAddressValue value) {
    pkg.ArrayAddressValueFM.toMemorySegment(
        value, values$MemorySegment(ms, index), allocator);
  }

  public static pkg.ArrayAddressValue[] values$Array(java.lang.foreign.MemorySegment ms) {
    var value = new pkg.ArrayAddressValue[(int) FM$DIMENSION$values$0];
    for (long index = 0; index < value.length; index++) {
      value[(int) index] = values(ms, index);
    }
    return value;
  }

  public static pkg.ArrayAddressValue[] values(java.lang.foreign.MemorySegment ms) {
    return values$Array(ms);
  }

  public static void values(java.lang.foreign.MemorySegment ms,
      java.lang.foreign.SegmentAllocator allocator, pkg.ArrayAddressValue[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != FM$DIMENSION$values$0) {
      throw new IllegalArgumentException(
          "values length must be " + FM$DIMENSION$values$0);
    }
    for (long index = 0; index < value.length; index++) {
      values(ms, allocator, index, value[(int) index]);
    }
  }
}

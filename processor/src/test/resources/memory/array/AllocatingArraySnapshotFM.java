package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class AllocatingArraySnapshotFM {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        MemoryLayout.sequenceLayout(2L,
            pkg.ArrayAddressValueFM.FM$LAYOUT).withName("values"),
      }));

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

  public static AllocatingArraySnapshot reinterpret(MemorySegment ms) {
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

  public static AllocatingArraySnapshot at(MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(
      AllocatingArraySnapshot from, MemorySegment ms, SegmentAllocator ff$allocator) {
    values(ms, ff$allocator, from.values());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, AllocatingArraySnapshot from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static AllocatingArraySnapshot fromMemorySegment(MemorySegment ms) {
    return new AllocatingArraySnapshot(
        values(ms));
  }

  public static final MemoryLayout.PathElement FM$PE$values =
      MemoryLayout.PathElement.groupElement("values");

  public static final MemoryLayout.PathElement FM$PE$values$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$values =
      FM$LAYOUT.select(FM$PE$values);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$values =
      pkg.ArrayAddressValueFM.FM$LAYOUT;

  public static final long FM$OFFSET$values =
      FM$LAYOUT.byteOffset(FM$PE$values);

  public static final long FM$SIZE$values =
      FM$LAYOUT$values.byteSize();

  public static final long FM$DIMENSION$values$0 = 2L;

  public static MemorySegment values$MemorySegment(MemorySegment ms) {
    return ms.asSlice(FM$OFFSET$values, FM$SIZE$values);
  }

  public static MemorySegment values$MemorySegment(
      MemorySegment ms, long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$values,
            MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$values.byteSize());
  }

  public static pkg.ArrayAddressValue values(
      MemorySegment ms, long index) {
    return pkg.ArrayAddressValueFM.fromMemorySegment(
        values$MemorySegment(ms, index));
  }

  public static void values(MemorySegment ms, SegmentAllocator allocator, long index,
      pkg.ArrayAddressValue value) {
    pkg.ArrayAddressValueFM.toMemorySegment(
        value, values$MemorySegment(ms, index), allocator);
  }

  public static pkg.ArrayAddressValue[] values$Array(MemorySegment ms) {
    var value = new pkg.ArrayAddressValue[(int) FM$DIMENSION$values$0];
    for (long index = 0; index < value.length; index++) {
      value[(int) index] = values(ms, index);
    }
    return value;
  }

  public static pkg.ArrayAddressValue[] values(MemorySegment ms) {
    return values$Array(ms);
  }

  public static void values(MemorySegment ms,
      SegmentAllocator allocator, pkg.ArrayAddressValue[] value) {
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

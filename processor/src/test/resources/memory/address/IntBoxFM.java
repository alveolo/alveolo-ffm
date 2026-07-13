package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class IntBoxFM {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("value"),
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

  public static IntBox reinterpret(java.lang.foreign.MemorySegment ms) {
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

  public static IntBox at(java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(
      IntBox from, java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator ff$allocator) {
    value(ms, ff$allocator, from.value());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment(
      java.lang.foreign.SegmentAllocator allocator, IntBox from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static IntBox fromMemorySegment(java.lang.foreign.MemorySegment ms) {
    return new IntBox(
        value(ms));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$value =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("value");

  public static final java.lang.invoke.VarHandle FM$VH$value =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$value), 1, 0L);

  public static int value(java.lang.foreign.MemorySegment ms) {
    return ((java.lang.foreign.MemorySegment) FM$VH$value.get(ms)).reinterpret(java.lang.foreign.ValueLayout.JAVA_INT.byteSize())
        .get(java.lang.foreign.ValueLayout.JAVA_INT, 0L);
  }

  public static void value(
      java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator allocator, int value) {
    var address = allocator.allocate(java.lang.foreign.ValueLayout.JAVA_INT);
    address.set(java.lang.foreign.ValueLayout.JAVA_INT, 0L, value);
    FM$VH$value.set(ms, address);
  }
}

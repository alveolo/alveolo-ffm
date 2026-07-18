package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class IntBoxFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("value"),
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

  public static IntBox reinterpret$F(
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

  public static IntBox at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      IntBox source,
      java.lang.foreign.MemorySegment memorySegment,
      java.lang.foreign.SegmentAllocator allocator) {
    value(memorySegment, allocator, source.value());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      IntBox source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment, allocator);
    return memorySegment;
  }

  public static IntBox fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new IntBox(
        value(memorySegment));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      value$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("value");

  public static final java.lang.invoke.VarHandle value$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(value$PathElement$F), 1, 0L);

  public static int value(java.lang.foreign.MemorySegment memorySegment) {
    return org.alveolo.ffm.NativeTypes.getWCharT(((java.lang.foreign.MemorySegment) value$VarHandle$F.get(memorySegment)).reinterpret(org.alveolo.ffm.NativeTypes.WCHAR_T_LAYOUT.byteSize()), 0L);
  }

  public static void value(
      java.lang.foreign.MemorySegment memorySegment, java.lang.foreign.SegmentAllocator allocator, int value) {
    var address = allocator.allocate(org.alveolo.ffm.NativeTypes.WCHAR_T_LAYOUT);
    org.alveolo.ffm.NativeTypes.setWCharT(address, 0L, value);
    value$VarHandle$F.set(memorySegment, address);
  }
}

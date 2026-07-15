package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class CircularAddress implements CircularAddressSpec {
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

  public static CircularAddress reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new CircularAddress(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static CircularAddress at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new CircularAddress(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public CircularAddress(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public CircularAddress(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      value$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("value");

  public static final java.lang.invoke.VarHandle value$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(value$PathElement$F), 1, 0L);

  public passmode.CircularDefault value() {
    return passmode.CircularDefault.reinterpret$F((java.lang.foreign.MemorySegment) value$VarHandle$F.get(MemorySegment$F));
  }

  public CircularAddress value(passmode.CircularDefault value) {
    value$VarHandle$F.set(MemorySegment$F, value.MemorySegment$F);
    return this;
  }
}

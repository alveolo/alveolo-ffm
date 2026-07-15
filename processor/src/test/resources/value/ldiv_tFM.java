package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ldiv_tFM implements ldiv_t {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("quot"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("rem"),
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

  public static ldiv_tFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new ldiv_tFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static ldiv_tFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new ldiv_tFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public ldiv_tFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public ldiv_tFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      quot$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("quot");

  public static final java.lang.invoke.VarHandle quot$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(quot$PathElement$F), 1, 0L);

  public int quot() {
    return (int) quot$VarHandle$F.get(MemorySegment$F);
  }

  public ldiv_tFM quot(int value) {
    quot$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      rem$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("rem");

  public static final java.lang.invoke.VarHandle rem$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(rem$PathElement$F), 1, 0L);

  public int rem() {
    return (int) rem$VarHandle$F.get(MemorySegment$F);
  }

  public ldiv_tFM rem(int value) {
    rem$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }
}

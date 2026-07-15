package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class timevalFM implements timeval {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("tv_sec"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("tv_usec"),
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

  public static timevalFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new timevalFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static timevalFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new timevalFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public timevalFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public timevalFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      tv_sec$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("tv_sec");

  public static final java.lang.invoke.VarHandle tv_sec$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(tv_sec$PathElement$F), 1, 0L);

  public int tv_sec() {
    return (int) tv_sec$VarHandle$F.get(MemorySegment$F);
  }

  public timevalFM tv_sec(int value) {
    tv_sec$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      tv_usec$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("tv_usec");

  public static final java.lang.invoke.VarHandle tv_usec$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(tv_usec$PathElement$F), 1, 0L);

  public int tv_usec() {
    return (int) tv_usec$VarHandle$F.get(MemorySegment$F);
  }

  public timevalFM tv_usec(int value) {
    tv_usec$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }
}

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

  public static ldiv_tFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new ldiv_tFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static ldiv_tFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new ldiv_tFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public ldiv_tFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public ldiv_tFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
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

  public ldiv_tFM quot(int value$f) {
    quot$VarHandle$F.set(MemorySegment$F, value$f);
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

  public ldiv_tFM rem(int value$f) {
    rem$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }
}

package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class UnionFM implements Union {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.unionLayout(
          org.alveolo.ffm.ForeignUtils.unionPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("i"),
        java.lang.foreign.ValueLayout.JAVA_DOUBLE.withName("d"),
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

  public static UnionFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new UnionFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static UnionFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new UnionFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public UnionFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public UnionFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      i$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("i");

  public static final java.lang.invoke.VarHandle i$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(i$PathElement$F), 1, 0L);

  public int i() {
    return (int) i$VarHandle$F.get(MemorySegment$F);
  }

  public UnionFM i(int value$f) {
    i$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      d$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("d");

  public static final java.lang.invoke.VarHandle d$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(d$PathElement$F), 1, 0L);

  public double d() {
    return (double) d$VarHandle$F.get(MemorySegment$F);
  }

  public UnionFM d(double value$f) {
    d$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }
}

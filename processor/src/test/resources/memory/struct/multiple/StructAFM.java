package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class StructAFM implements StructA {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("x"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("y"),
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

  public static StructAFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new StructAFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static StructAFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new StructAFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public StructAFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public StructAFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      x$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("x");

  public static final java.lang.invoke.VarHandle x$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(x$PathElement$F), 1, 0L);

  public int x() {
    return (int) x$VarHandle$F.get(MemorySegment$F);
  }

  public StructAFM x(int value$f) {
    x$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      y$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("y");

  public static final java.lang.invoke.VarHandle y$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(y$PathElement$F), 1, 0L);

  public int y() {
    return (int) y$VarHandle$F.get(MemorySegment$F);
  }

  public StructAFM y(int value$f) {
    y$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }
}

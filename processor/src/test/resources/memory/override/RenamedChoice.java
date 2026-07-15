package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class RenamedChoice implements SimpleChoice {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.unionLayout(
          org.alveolo.ffm.ForeignUtils.unionPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("i"),
        java.lang.foreign.ValueLayout.JAVA_FLOAT.withName("f"),
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

  public static RenamedChoice reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new RenamedChoice(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static RenamedChoice at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new RenamedChoice(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public RenamedChoice(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public RenamedChoice(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
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

  public RenamedChoice i(int value) {
    i$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      f$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("f");

  public static final java.lang.invoke.VarHandle f$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(f$PathElement$F), 1, 0L);

  public float f() {
    return (float) f$VarHandle$F.get(MemorySegment$F);
  }

  public RenamedChoice f(float value) {
    f$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }
}

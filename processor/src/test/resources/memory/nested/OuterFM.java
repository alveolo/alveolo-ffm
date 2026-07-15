package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class OuterFM implements Outer {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("inner"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("tag"),
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

  public static OuterFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new OuterFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static OuterFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new OuterFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public OuterFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public OuterFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      inner$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("inner");

  public static final java.lang.invoke.VarHandle inner$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(inner$PathElement$F), 1, 0L);

  public pkg.Inner inner() {
    return pkg.InnerFM.reinterpret$F((java.lang.foreign.MemorySegment) inner$VarHandle$F.get(MemorySegment$F));
  }

  public OuterFM inner(pkg.Inner value) {
    inner$VarHandle$F.set(MemorySegment$F, ((pkg.InnerFM) value).MemorySegment$F);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      tag$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("tag");

  public static final java.lang.invoke.VarHandle tag$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(tag$PathElement$F), 1, 0L);

  public int tag() {
    return (int) tag$VarHandle$F.get(MemorySegment$F);
  }

  public OuterFM tag(int value) {
    tag$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }
}

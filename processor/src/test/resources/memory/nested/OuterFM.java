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

  public static OuterFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new OuterFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static OuterFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new OuterFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public OuterFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public OuterFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
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

  public OuterFM inner(pkg.Inner value$f) {
    inner$VarHandle$F.set(MemorySegment$F, ((pkg.InnerFM) value$f).MemorySegment$F);
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

  public OuterFM tag(int value$f) {
    tag$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }
}

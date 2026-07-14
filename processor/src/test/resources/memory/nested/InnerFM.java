package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class InnerFM implements Inner {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("a"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("b"),
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

  public static InnerFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new InnerFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static InnerFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new InnerFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public InnerFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public InnerFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      a$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("a");

  public static final java.lang.invoke.VarHandle a$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(a$PathElement$F), 1, 0L);

  public int a() {
    return (int) a$VarHandle$F.get(MemorySegment$F);
  }

  public InnerFM a(int value$f) {
    a$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      b$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("b");

  public static final java.lang.invoke.VarHandle b$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(b$PathElement$F), 1, 0L);

  public int b() {
    return (int) b$VarHandle$F.get(MemorySegment$F);
  }

  public InnerFM b(int value$f) {
    b$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }
}

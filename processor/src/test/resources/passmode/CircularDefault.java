package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class CircularDefault implements CircularDefaultSpec {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        passmode.CircularValue.MemoryLayout$F.withName("value"),
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

  public static CircularDefault reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new CircularDefault(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static CircularDefault at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new CircularDefault(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public CircularDefault(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public CircularDefault(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      value$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("value");

  public passmode.CircularValue value() {
    return new passmode.CircularValue(MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(value$PathElement$F),
        MemoryLayout$F.select(value$PathElement$F).byteSize()));
  }

  public CircularDefault value(passmode.CircularValue value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(value$PathElement$F);
    var slice$f = MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(value$PathElement$F),
        memoryLayout$f.byteSize());
    java.lang.foreign.MemorySegment.copy(
        value$f.MemorySegment$F, 0,
        slice$f, 0, memoryLayout$f.byteSize());
    return this;
  }
}

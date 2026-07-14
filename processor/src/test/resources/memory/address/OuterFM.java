package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class OuterFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        pkg.PairBoxFM.MemoryLayout$F.withName("box"),
        pkg.IntBoxFM.MemoryLayout$F.withName("intBox"),
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

  public static Outer reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return fromMemorySegment$F(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static Outer at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return fromMemorySegment$F(elementAt$F(array$f, index$f));
  }

  public static void toMemorySegment$F(
      Outer source$f,
      java.lang.foreign.MemorySegment memorySegment$f,
      java.lang.foreign.SegmentAllocator allocator$f) {
    box(memorySegment$f, allocator$f, source$f.box());
    intBox(memorySegment$f, allocator$f, source$f.intBox());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator$f,
      Outer source$f) {
    var memorySegment$f = allocate$F(allocator$f);
    toMemorySegment$F(source$f, memorySegment$f, allocator$f);
    return memorySegment$f;
  }

  public static Outer fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new Outer(
        box(memorySegment$f),
        intBox(memorySegment$f));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      box$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("box");

  public static pkg.PairBox box(java.lang.foreign.MemorySegment memorySegment$f) {
    return pkg.PairBoxFM.fromMemorySegment$F(memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(box$PathElement$F),
        MemoryLayout$F.select(box$PathElement$F).byteSize()));
  }

  public static void box(
      java.lang.foreign.MemorySegment memorySegment$f, java.lang.foreign.SegmentAllocator allocator$f, pkg.PairBox value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(box$PathElement$F);
    var slice$f = memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(box$PathElement$F),
        memoryLayout$f.byteSize());
    pkg.PairBoxFM.toMemorySegment$F(
        value$f, slice$f, allocator$f);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      intBox$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("intBox");

  public static pkg.IntBox intBox(java.lang.foreign.MemorySegment memorySegment$f) {
    return pkg.IntBoxFM.fromMemorySegment$F(memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(intBox$PathElement$F),
        MemoryLayout$F.select(intBox$PathElement$F).byteSize()));
  }

  public static void intBox(
      java.lang.foreign.MemorySegment memorySegment$f, java.lang.foreign.SegmentAllocator allocator$f, pkg.IntBox value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(intBox$PathElement$F);
    var slice$f = memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(intBox$PathElement$F),
        memoryLayout$f.byteSize());
    pkg.IntBoxFM.toMemorySegment$F(
        value$f, slice$f, allocator$f);
  }
}

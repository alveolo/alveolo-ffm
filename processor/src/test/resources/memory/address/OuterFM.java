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
      java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocateSequence$F(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(MemoryLayout$F, count);
  }

  public static Outer reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : fromMemorySegment$F(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpretSequence$F(
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

  public static Outer at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      Outer source,
      java.lang.foreign.MemorySegment memorySegment) {
    box(allocator, memorySegment, source.box());
    intBox(allocator, memorySegment, source.intBox());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      Outer source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(allocator, source, memorySegment);
    return memorySegment;
  }

  public static Outer fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new Outer(
        box(memorySegment),
        intBox(memorySegment));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      box$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("box");

  public static pkg.PairBox box(java.lang.foreign.MemorySegment memorySegment) {
    return pkg.PairBoxFM.fromMemorySegment$F(memorySegment.asSlice(
        MemoryLayout$F.byteOffset(box$PathElement$F),
        MemoryLayout$F.select(box$PathElement$F).byteSize()));
  }

  public static void box(
      java.lang.foreign.SegmentAllocator allocator, java.lang.foreign.MemorySegment memorySegment, pkg.PairBox value) {
    var memoryLayout =
        MemoryLayout$F.select(box$PathElement$F);
    var slice = memorySegment.asSlice(
        MemoryLayout$F.byteOffset(box$PathElement$F),
        memoryLayout.byteSize());
    pkg.PairBoxFM.toMemorySegment$F(
        allocator, value, slice);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      intBox$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("intBox");

  public static pkg.IntBox intBox(java.lang.foreign.MemorySegment memorySegment) {
    return pkg.IntBoxFM.fromMemorySegment$F(memorySegment.asSlice(
        MemoryLayout$F.byteOffset(intBox$PathElement$F),
        MemoryLayout$F.select(intBox$PathElement$F).byteSize()));
  }

  public static void intBox(
      java.lang.foreign.SegmentAllocator allocator, java.lang.foreign.MemorySegment memorySegment, pkg.IntBox value) {
    var memoryLayout =
        MemoryLayout$F.select(intBox$PathElement$F);
    var slice = memorySegment.asSlice(
        MemoryLayout$F.byteOffset(intBox$PathElement$F),
        memoryLayout.byteSize());
    pkg.IntBoxFM.toMemorySegment$F(
        allocator, value, slice);
  }
}

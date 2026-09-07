package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public class IndexedDerivedFM extends pkg.IndexedBaseFM
    implements IndexedDerived {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        pkg.IndexedBaseFM.MemoryLayout$F,
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

  public static IndexedDerivedFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : new IndexedDerivedFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static IndexedDerivedFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new IndexedDerivedFM(elementAt$F(array, index));
  }

  public IndexedDerivedFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public IndexedDerivedFM(
      java.lang.foreign.MemorySegment memorySegment) {
    super(memorySegment);
  }

  @Override
  public IndexedDerivedFM values(
      long index0$f,
      int value$f) {
    return (IndexedDerivedFM) super.values(
        index0$f, value$f);
  }

  @Override
  public IndexedDerivedFM valuesFromArray$F(int[] value) {
    return (IndexedDerivedFM) super.valuesFromArray$F(value);
  }

  @Override
  public IndexedDerivedFM pointers(
      long index0$f,
      java.lang.foreign.MemorySegment value$f) {
    return (IndexedDerivedFM) super.pointers(
        index0$f, value$f);
  }

  @Override
  public IndexedDerivedFM pointersAsAddress$F(
      long index0, java.lang.foreign.MemorySegment value) {
    return (IndexedDerivedFM) super.pointersAsAddress$F(index0, value);
  }
}

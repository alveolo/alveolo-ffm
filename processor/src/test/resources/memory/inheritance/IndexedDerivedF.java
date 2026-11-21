package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public class IndexedDerivedF extends pkg.IndexedBaseF
    implements IndexedDerived {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        pkg.IndexedBaseF.MemoryLayout$F,
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

  public static IndexedDerivedF reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : new IndexedDerivedF(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static IndexedDerivedF at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new IndexedDerivedF(elementAt$F(array, index));
  }

  public IndexedDerivedF(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public IndexedDerivedF(
      java.lang.foreign.MemorySegment memorySegment) {
    super(memorySegment);
  }

  @Override
  public IndexedDerivedF values(
      long index0$f,
      int value$f) {
    return (IndexedDerivedF) super.values(
        index0$f, value$f);
  }

  @Override
  public IndexedDerivedF valuesFromArray$F(int[] value) {
    return (IndexedDerivedF) super.valuesFromArray$F(value);
  }

  @Override
  public IndexedDerivedF pointers(
      long index0$f,
      java.lang.foreign.MemorySegment value$f) {
    return (IndexedDerivedF) super.pointers(
        index0$f, value$f);
  }

  @Override
  public IndexedDerivedF pointersAsAddress$F(
      long index0, java.lang.foreign.MemorySegment value) {
    return (IndexedDerivedF) super.pointersAsAddress$F(index0, value);
  }
}

package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class PairBoxFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("pair"),
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

  public static PairBox reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return fromMemorySegment$F(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static PairBox at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      PairBox source,
      java.lang.foreign.MemorySegment memorySegment,
      java.lang.foreign.SegmentAllocator allocator) {
    pair(memorySegment, allocator, source.pair());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      PairBox source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment, allocator);
    return memorySegment;
  }

  public static PairBox fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new PairBox(
        pair(memorySegment));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      pair$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("pair");

  public static final java.lang.invoke.VarHandle pair$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(pair$PathElement$F), 1, 0L);

  public static pkg.Pair pair(java.lang.foreign.MemorySegment memorySegment) {
    return pkg.PairFM.reinterpret$F((java.lang.foreign.MemorySegment) pair$VarHandle$F.get(memorySegment));
  }

  public static void pair(
      java.lang.foreign.MemorySegment memorySegment, java.lang.foreign.SegmentAllocator allocator, pkg.Pair value) {
    pair$VarHandle$F.set(memorySegment,
        pkg.PairFM.toMemorySegment$F(allocator, value));
  }
}

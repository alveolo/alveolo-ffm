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

  public static PairBox reinterpret$F(
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

  public static PairBox at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return fromMemorySegment$F(elementAt$F(array$f, index$f));
  }

  public static void toMemorySegment$F(
      PairBox source$f,
      java.lang.foreign.MemorySegment memorySegment$f,
      java.lang.foreign.SegmentAllocator allocator$f) {
    pair(memorySegment$f, allocator$f, source$f.pair());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator$f,
      PairBox source$f) {
    var memorySegment$f = allocate$F(allocator$f);
    toMemorySegment$F(source$f, memorySegment$f, allocator$f);
    return memorySegment$f;
  }

  public static PairBox fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new PairBox(
        pair(memorySegment$f));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      pair$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("pair");

  public static final java.lang.invoke.VarHandle pair$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(pair$PathElement$F), 1, 0L);

  public static pkg.Pair pair(java.lang.foreign.MemorySegment memorySegment$f) {
    return pkg.PairFM.reinterpret$F((java.lang.foreign.MemorySegment) pair$VarHandle$F.get(memorySegment$f));
  }

  public static void pair(
      java.lang.foreign.MemorySegment memorySegment$f, java.lang.foreign.SegmentAllocator allocator$f, pkg.Pair value$f) {
    pair$VarHandle$F.set(memorySegment$f,
        pkg.PairFM.toMemorySegment$F(allocator$f, value$f));
  }
}

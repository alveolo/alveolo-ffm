package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class PairBoxFM {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("pair"),
      }));

  public static java.lang.foreign.MemorySegment allocate(java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static PairBox reinterpret(java.lang.foreign.MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret(
      java.lang.foreign.MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment FM$at(java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static PairBox at(java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(
      PairBox from, java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator ff$allocator) {
    pair(ms, ff$allocator, from.pair());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment(
      java.lang.foreign.SegmentAllocator allocator, PairBox from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static PairBox fromMemorySegment(java.lang.foreign.MemorySegment ms) {
    return new PairBox(
        pair(ms));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$pair =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("pair");

  public static final java.lang.invoke.VarHandle FM$VH$pair =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$pair), 1, 0L);

  public static pkg.Pair pair(java.lang.foreign.MemorySegment ms) {
    return pkg.PairFM.reinterpret((java.lang.foreign.MemorySegment) FM$VH$pair.get(ms));
  }

  public static void pair(
      java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator allocator, pkg.Pair value) {
    FM$VH$pair.set(ms,
        pkg.PairFM.toMemorySegment(allocator, value));
  }
}

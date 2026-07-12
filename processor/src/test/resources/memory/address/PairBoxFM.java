package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class PairBoxFM {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        ValueLayout.ADDRESS.withName("pair"),
      }));

  public static final MemoryLayout.PathElement FM$PE$pair =
      MemoryLayout.PathElement.groupElement("pair");

  public static final java.lang.invoke.VarHandle FM$VH$pair =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$pair), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static MemorySegment allocate(
      SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static PairBox reinterpret(MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static MemorySegment reinterpret(
      MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static MemorySegment FM$at(MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static PairBox at(MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(
      PairBox from, MemorySegment ms, SegmentAllocator ff$allocator) {
    pair(ms, ff$allocator, from.pair());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, PairBox from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static PairBox fromMemorySegment(MemorySegment ms) {
    return new PairBox(
        pair(ms));
  }

  public static pkg.Pair pair(MemorySegment ms) {
    return pkg.PairFM.reinterpret((MemorySegment) FM$VH$pair.get(ms));
  }

  public static void pair(
      MemorySegment ms, SegmentAllocator allocator, pkg.Pair value) {
    FM$VH$pair.set(ms,
        pkg.PairFM.toMemorySegment(allocator, value));
  }
}

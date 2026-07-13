package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class OuterFM {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        pkg.PairBoxFM.FM$LAYOUT.withName("box"),
        pkg.IntBoxFM.FM$LAYOUT.withName("intBox"),
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

  public static Outer reinterpret(java.lang.foreign.MemorySegment ms) {
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

  public static Outer at(java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(
      Outer from, java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator ff$allocator) {
    box(ms, ff$allocator, from.box());
    intBox(ms, ff$allocator, from.intBox());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment(
      java.lang.foreign.SegmentAllocator allocator, Outer from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static Outer fromMemorySegment(java.lang.foreign.MemorySegment ms) {
    return new Outer(
        box(ms),
        intBox(ms));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$box =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("box");

  public static pkg.PairBox box(java.lang.foreign.MemorySegment ms) {
    return pkg.PairBoxFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$box),
        FM$LAYOUT.select(FM$PE$box).byteSize()));
  }

  public static void box(
      java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator allocator, pkg.PairBox value) {
    var layout = FM$LAYOUT.select(FM$PE$box);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$box), layout.byteSize());
    pkg.PairBoxFM.toMemorySegment(value, slice, allocator);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$intBox =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("intBox");

  public static pkg.IntBox intBox(java.lang.foreign.MemorySegment ms) {
    return pkg.IntBoxFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$intBox),
        FM$LAYOUT.select(FM$PE$intBox).byteSize()));
  }

  public static void intBox(
      java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator allocator, pkg.IntBox value) {
    var layout = FM$LAYOUT.select(FM$PE$intBox);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$intBox), layout.byteSize());
    pkg.IntBoxFM.toMemorySegment(value, slice, allocator);
  }
}

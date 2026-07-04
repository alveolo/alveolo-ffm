package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class OuterFM {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        pkg.PairBoxFM.FM$LAYOUT.withName("box"),
        pkg.IntBoxFM.FM$LAYOUT.withName("intBox"),
      }));

  public static final MemoryLayout.PathElement FM$PE$box =
      MemoryLayout.PathElement.groupElement("box");

  public static final MemoryLayout.PathElement FM$PE$intBox =
      MemoryLayout.PathElement.groupElement("intBox");

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static Outer reinterpret(MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static void toMemorySegment(
      Outer from, MemorySegment ms, SegmentAllocator ff$allocator) {
    box(ms, ff$allocator, from.box());
    intBox(ms, ff$allocator, from.intBox());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, Outer from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static Outer fromMemorySegment(MemorySegment ms) {
    return new Outer(
        box(ms),
        intBox(ms));
  }

  public static pkg.PairBox box(MemorySegment ms) {
    return pkg.PairBoxFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$box),
        FM$LAYOUT.select(FM$PE$box).byteSize()));
  }

  public static void box(
      MemorySegment ms, SegmentAllocator allocator, pkg.PairBox value) {
    var layout = FM$LAYOUT.select(FM$PE$box);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$box), layout.byteSize());
    pkg.PairBoxFM.toMemorySegment(value, slice, allocator);
  }

  public static pkg.IntBox intBox(MemorySegment ms) {
    return pkg.IntBoxFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$intBox),
        FM$LAYOUT.select(FM$PE$intBox).byteSize()));
  }

  public static void intBox(
      MemorySegment ms, SegmentAllocator allocator, pkg.IntBox value) {
    var layout = FM$LAYOUT.select(FM$PE$intBox);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$intBox), layout.byteSize());
    pkg.IntBoxFM.toMemorySegment(value, slice, allocator);
  }
}

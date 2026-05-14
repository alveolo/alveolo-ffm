package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class OuterFM implements Outer {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
            pkg.InnerFM.FM$LAYOUT.withName("inner"),
            ValueLayout.JAVA_INT.withName("tag"),
          }));

  public static final MemoryLayout.PathElement FM$PE$inner =
      MemoryLayout.PathElement.groupElement("inner");

  public static final MemoryLayout.PathElement FM$PE$tag =
      MemoryLayout.PathElement.groupElement("tag");

  public static final java.lang.invoke.VarHandle FM$VH$tag =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$tag), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
        FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public final MemorySegment ms;

  public OuterFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public OuterFM(MemorySegment ms) {
    this.ms = ms;
  }

  public pkg.InnerFM inner() {
    return new pkg.InnerFM(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$inner),
        FM$LAYOUT.select(FM$PE$inner).byteSize()));
  }

  public void inner(pkg.InnerFM value) {
    var layout = FM$LAYOUT.select(FM$PE$inner);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$inner), layout.byteSize());
    MemorySegment.copy(value.ms, 0, slice, 0, layout.byteSize());
  }

  public int tag() {
    return (int) FM$VH$tag.get(ms);
  }

  public OuterFM tag(int value) {
    FM$VH$tag.set(ms, value);
    return this;
  }
}

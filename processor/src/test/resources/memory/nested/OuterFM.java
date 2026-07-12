package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class OuterFM implements Outer {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        ValueLayout.ADDRESS.withName("inner"),
        ValueLayout.JAVA_INT.withName("tag"),
      }));

  public static final MemoryLayout.PathElement FM$PE$inner =
      MemoryLayout.PathElement.groupElement("inner");

  public static final MemoryLayout.PathElement FM$PE$tag =
      MemoryLayout.PathElement.groupElement("tag");

  public static final java.lang.invoke.VarHandle FM$VH$inner =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$inner), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$tag =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$tag), 1, 0L);

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

  public static OuterFM reinterpret(MemorySegment ms) {
    return new OuterFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static OuterFM at(MemorySegment array, long index) {
    return new OuterFM(FM$at(array, index));
  }

  public final MemorySegment ms;

  public OuterFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public OuterFM(MemorySegment ms) {
    this.ms = ms;
  }

  public pkg.Inner inner() {
    return pkg.InnerFM.reinterpret((MemorySegment) FM$VH$inner.get(ms));
  }

  public OuterFM inner(pkg.Inner value) {
    FM$VH$inner.set(ms, ((pkg.InnerFM) value).ms);
    return this;
  }

  public int tag() {
    return (int) FM$VH$tag.get(ms);
  }

  public OuterFM tag(int value) {
    FM$VH$tag.set(ms, value);
    return this;
  }
}

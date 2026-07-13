package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class OuterFM implements Outer {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("inner"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("tag"),
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

  public static OuterFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new OuterFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static OuterFM at(java.lang.foreign.MemorySegment array, long index) {
    return new OuterFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public OuterFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public OuterFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$inner =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("inner");

  public static final java.lang.invoke.VarHandle FM$VH$inner =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$inner), 1, 0L);

  public pkg.Inner inner() {
    return pkg.InnerFM.reinterpret((java.lang.foreign.MemorySegment) FM$VH$inner.get(ms));
  }

  public OuterFM inner(pkg.Inner value) {
    FM$VH$inner.set(ms, ((pkg.InnerFM) value).ms);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$tag =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("tag");

  public static final java.lang.invoke.VarHandle FM$VH$tag =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$tag), 1, 0L);

  public int tag() {
    return (int) FM$VH$tag.get(ms);
  }

  public OuterFM tag(int value) {
    FM$VH$tag.set(ms, value);
    return this;
  }
}

package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class StructAFM implements StructA {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("x"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("y"),
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

  public static StructAFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new StructAFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static StructAFM at(java.lang.foreign.MemorySegment array, long index) {
    return new StructAFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public StructAFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public StructAFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$x =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("x");

  public static final java.lang.invoke.VarHandle FM$VH$x =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$x), 1, 0L);

  public int x() {
    return (int) FM$VH$x.get(ms);
  }

  public StructAFM x(int value) {
    FM$VH$x.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$y =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("y");

  public static final java.lang.invoke.VarHandle FM$VH$y =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$y), 1, 0L);

  public int y() {
    return (int) FM$VH$y.get(ms);
  }

  public StructAFM y(int value) {
    FM$VH$y.set(ms, value);
    return this;
  }
}

package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ldiv_tFM implements ldiv_t {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("quot"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("rem"),
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

  public static ldiv_tFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new ldiv_tFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static ldiv_tFM at(java.lang.foreign.MemorySegment array, long index) {
    return new ldiv_tFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public ldiv_tFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public ldiv_tFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$quot =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("quot");

  public static final java.lang.invoke.VarHandle FM$VH$quot =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$quot), 1, 0L);

  public int quot() {
    return (int) FM$VH$quot.get(ms);
  }

  public ldiv_tFM quot(int value) {
    FM$VH$quot.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$rem =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("rem");

  public static final java.lang.invoke.VarHandle FM$VH$rem =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$rem), 1, 0L);

  public int rem() {
    return (int) FM$VH$rem.get(ms);
  }

  public ldiv_tFM rem(int value) {
    FM$VH$rem.set(ms, value);
    return this;
  }
}

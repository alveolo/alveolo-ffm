package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class timevalFM implements timeval {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("tv_sec"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("tv_usec"),
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

  public static timevalFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new timevalFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static timevalFM at(java.lang.foreign.MemorySegment array, long index) {
    return new timevalFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public timevalFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public timevalFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$tv_sec =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("tv_sec");

  public static final java.lang.invoke.VarHandle FM$VH$tv_sec =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$tv_sec), 1, 0L);

  public int tv_sec() {
    return (int) FM$VH$tv_sec.get(ms);
  }

  public timevalFM tv_sec(int value) {
    FM$VH$tv_sec.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$tv_usec =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("tv_usec");

  public static final java.lang.invoke.VarHandle FM$VH$tv_usec =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$tv_usec), 1, 0L);

  public int tv_usec() {
    return (int) FM$VH$tv_usec.get(ms);
  }

  public timevalFM tv_usec(int value) {
    FM$VH$tv_usec.set(ms, value);
    return this;
  }
}

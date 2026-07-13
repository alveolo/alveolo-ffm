package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class InnerFM implements Inner {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("a"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("b"),
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

  public static InnerFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new InnerFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static InnerFM at(java.lang.foreign.MemorySegment array, long index) {
    return new InnerFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public InnerFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public InnerFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$a =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("a");

  public static final java.lang.invoke.VarHandle FM$VH$a =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$a), 1, 0L);

  public int a() {
    return (int) FM$VH$a.get(ms);
  }

  public InnerFM a(int value) {
    FM$VH$a.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$b =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("b");

  public static final java.lang.invoke.VarHandle FM$VH$b =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$b), 1, 0L);

  public int b() {
    return (int) FM$VH$b.get(ms);
  }

  public InnerFM b(int value) {
    FM$VH$b.set(ms, value);
    return this;
  }
}

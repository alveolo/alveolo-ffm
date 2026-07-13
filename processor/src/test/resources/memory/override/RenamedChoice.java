package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class RenamedChoice implements SimpleChoice {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.unionLayout(
          org.alveolo.ffm.ForeignUtils.unionPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("i"),
        java.lang.foreign.ValueLayout.JAVA_FLOAT.withName("f"),
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

  public static RenamedChoice reinterpret(java.lang.foreign.MemorySegment ms) {
    return new RenamedChoice(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static RenamedChoice at(java.lang.foreign.MemorySegment array, long index) {
    return new RenamedChoice(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public RenamedChoice(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public RenamedChoice(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$i =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("i");

  public static final java.lang.invoke.VarHandle FM$VH$i =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$i), 1, 0L);

  public int i() {
    return (int) FM$VH$i.get(ms);
  }

  public RenamedChoice i(int value) {
    FM$VH$i.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$f =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("f");

  public static final java.lang.invoke.VarHandle FM$VH$f =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$f), 1, 0L);

  public float f() {
    return (float) FM$VH$f.get(ms);
  }

  public RenamedChoice f(float value) {
    FM$VH$f.set(ms, value);
    return this;
  }
}

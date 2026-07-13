package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class RenamedPoint {
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

  public static SimpleOverrides reinterpret(java.lang.foreign.MemorySegment ms) {
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

  public static SimpleOverrides at(java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(SimpleOverrides from, java.lang.foreign.MemorySegment ms) {
    x(ms, from.x());
    y(ms, from.y());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment(
      java.lang.foreign.SegmentAllocator allocator, SimpleOverrides from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms);
    return ms;
  }

  public static SimpleOverrides fromMemorySegment(java.lang.foreign.MemorySegment ms) {
    return new SimpleOverrides(
        x(ms),
        y(ms));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$x =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("x");

  public static final java.lang.invoke.VarHandle FM$VH$x =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$x), 1, 0L);

  public static int x(java.lang.foreign.MemorySegment ms) {
    return (int) FM$VH$x.get(ms);
  }

  public static void x(java.lang.foreign.MemorySegment ms, int value) {
    FM$VH$x.set(ms, value);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$y =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("y");

  public static final java.lang.invoke.VarHandle FM$VH$y =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$y), 1, 0L);

  public static int y(java.lang.foreign.MemorySegment ms) {
    return (int) FM$VH$y.get(ms);
  }

  public static void y(java.lang.foreign.MemorySegment ms, int value) {
    FM$VH$y.set(ms, value);
  }
}

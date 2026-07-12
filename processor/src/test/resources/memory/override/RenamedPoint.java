package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class RenamedPoint {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        ValueLayout.JAVA_INT.withName("x"),
        ValueLayout.JAVA_INT.withName("y"),
      }));

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

  public static SimpleOverrides reinterpret(MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static SimpleOverrides at(MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(SimpleOverrides from, MemorySegment ms) {
    x(ms, from.x());
    y(ms, from.y());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, SimpleOverrides from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms);
    return ms;
  }

  public static SimpleOverrides fromMemorySegment(MemorySegment ms) {
    return new SimpleOverrides(
        x(ms),
        y(ms));
  }

  public static final MemoryLayout.PathElement FM$PE$x =
      MemoryLayout.PathElement.groupElement("x");

  public static final java.lang.invoke.VarHandle FM$VH$x =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$x), 1, 0L);

  public static int x(MemorySegment ms) {
    return (int) FM$VH$x.get(ms);
  }

  public static void x(MemorySegment ms, int value) {
    FM$VH$x.set(ms, value);
  }

  public static final MemoryLayout.PathElement FM$PE$y =
      MemoryLayout.PathElement.groupElement("y");

  public static final java.lang.invoke.VarHandle FM$VH$y =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$y), 1, 0L);

  public static int y(MemorySegment ms) {
    return (int) FM$VH$y.get(ms);
  }

  public static void y(MemorySegment ms, int value) {
    FM$VH$y.set(ms, value);
  }
}

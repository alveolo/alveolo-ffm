package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class div_tFM {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        ValueLayout.JAVA_INT.withName("quot"),
        ValueLayout.JAVA_INT.withName("rem"),
      }));

  public static final MemoryLayout.PathElement FM$PE$quot =
      MemoryLayout.PathElement.groupElement("quot");

  public static final MemoryLayout.PathElement FM$PE$rem =
      MemoryLayout.PathElement.groupElement("rem");

  public static final java.lang.invoke.VarHandle FM$VH$quot =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$quot), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$rem =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$rem), 1, 0L);

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

  public static div_t reinterpret(MemorySegment ms) {
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

  public static div_t at(MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(div_t from, MemorySegment ms) {
    quot(ms, from.quot());
    rem(ms, from.rem());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, div_t from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms);
    return ms;
  }

  public static div_t fromMemorySegment(MemorySegment ms) {
    return new div_t(
        quot(ms),
        rem(ms));
  }

  public static int quot(MemorySegment ms) {
    return (int) FM$VH$quot.get(ms);
  }

  public static void quot(MemorySegment ms, int value) {
    FM$VH$quot.set(ms, value);
  }

  public static int rem(MemorySegment ms) {
    return (int) FM$VH$rem.get(ms);
  }

  public static void rem(MemorySegment ms, int value) {
    FM$VH$rem.set(ms, value);
  }
}

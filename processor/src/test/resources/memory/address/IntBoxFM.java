package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class IntBoxFM {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        ValueLayout.ADDRESS.withName("value"),
      }));

  public static final MemoryLayout.PathElement FM$PE$value =
      MemoryLayout.PathElement.groupElement("value");

  public static final java.lang.invoke.VarHandle FM$VH$value =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$value), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static IntBox reinterpret(MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static void toMemorySegment(
      IntBox from, MemorySegment ms, SegmentAllocator ff$allocator) {
    value(ms, ff$allocator, from.value());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, IntBox from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static IntBox fromMemorySegment(MemorySegment ms) {
    return new IntBox(value(ms));
  }

  public static int value(MemorySegment ms) {
    return ((MemorySegment) FM$VH$value.get(ms)).reinterpret(ValueLayout.JAVA_INT.byteSize())
        .get(ValueLayout.JAVA_INT, 0L);
  }

  public static void value(
      MemorySegment ms, SegmentAllocator allocator, int value) {
    var address = allocator.allocate(ValueLayout.JAVA_INT);
    address.set(ValueLayout.JAVA_INT, 0L, value);
    FM$VH$value.set(ms, address);
  }
}

package pkg;

import java.lang.foreign.*;
import java.lang.invoke.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignValueProcessor")
public final class div_tFM {
  public static final MemoryLayout FM$LAYOUT =
    MemoryLayout.structLayout(
      org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
        ValueLayout.JAVA_INT.withName("quot"),
        ValueLayout.JAVA_INT.withName("rem"),
      }));

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static void toMemorySegment(pkg.div_t from, MemorySegment ms) {
    quot(ms, from.quot());
    rem(ms, from.rem());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, pkg.div_t from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms);
    return ms;
  }

  public static pkg.div_t fromMemorySegment(MemorySegment ms) {
    return new pkg.div_t(quot(ms), rem(ms));
  }

  public static final MemoryLayout.PathElement FM$PE$quot =
    MemoryLayout.PathElement.groupElement("quot");

  public static final java.lang.invoke.VarHandle FM$VH$quot =
    MethodHandles.insertCoordinates(FM$LAYOUT.varHandle(FM$PE$quot), 1, 0L);

  public static int quot(MemorySegment ms) {
    return (int) FM$VH$quot.get(ms);
  }

  public static void quot(MemorySegment ms, int value) {
    FM$VH$quot.set(ms, value);
  }

  public static final MemoryLayout.PathElement FM$PE$rem =
    MemoryLayout.PathElement.groupElement("rem");

  public static final java.lang.invoke.VarHandle FM$VH$rem =
    MethodHandles.insertCoordinates(FM$LAYOUT.varHandle(FM$PE$rem), 1, 0L);

  public static int rem(MemorySegment ms) {
    return (int) FM$VH$rem.get(ms);
  }

  public static void rem(MemorySegment ms, int value) {
    FM$VH$rem.set(ms, value);
  }
}

package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ldiv_tFM implements ldiv_t {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
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

  public final MemorySegment ms;

  public ldiv_tFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public ldiv_tFM(MemorySegment ms) {
    this.ms = ms;
  }

  public int quot() {
    return (int) FM$VH$quot.get(ms);
  }

  public ldiv_tFM quot(int value) {
    FM$VH$quot.set(ms, value);
    return this;
  }

  public int rem() {
    return (int) FM$VH$rem.get(ms);
  }

  public ldiv_tFM rem(int value) {
    FM$VH$rem.set(ms, value);
    return this;
  }
}

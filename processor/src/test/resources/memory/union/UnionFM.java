package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class UnionFM implements Union {
  public static final MemoryLayout FM$LAYOUT =
    MemoryLayout.unionLayout(
        org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
          ValueLayout.JAVA_INT.withName("i"),
          ValueLayout.JAVA_DOUBLE.withName("d"),
        }));

  public static final MemoryLayout.PathElement FM$PE$i =
      MemoryLayout.PathElement.groupElement("i");

  public static final MemoryLayout.PathElement FM$PE$d =
      MemoryLayout.PathElement.groupElement("d");

  public static final java.lang.invoke.VarHandle FM$VH$i =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$i), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$d =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$d), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public final MemorySegment ms;

  public UnionFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public UnionFM(MemorySegment ms) {
    this.ms = ms;
  }

  public int i() {
    return (int) FM$VH$i.get(ms);
  }

  public UnionFM i(int value) {
    FM$VH$i.set(ms, value);
    return this;
  }

  public double d() {
    return (double) FM$VH$d.get(ms);
  }

  public UnionFM d(double value) {
    FM$VH$d.set(ms, value);
    return this;
  }
}

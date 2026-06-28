package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class StructAFM implements StructA {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
            ValueLayout.JAVA_INT.withName("x"),
            ValueLayout.JAVA_INT.withName("y"),
          }));

  public static final MemoryLayout.PathElement FM$PE$x =
      MemoryLayout.PathElement.groupElement("x");

  public static final MemoryLayout.PathElement FM$PE$y =
      MemoryLayout.PathElement.groupElement("y");

  public static final java.lang.invoke.VarHandle FM$VH$x =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$x), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$y =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$y), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
        FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static StructAFM reinterpret(MemorySegment ms) {
    return new StructAFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public StructAFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public StructAFM(MemorySegment ms) {
    this.ms = ms;
  }

  public int x() {
    return (int) FM$VH$x.get(ms);
  }

  public StructAFM x(int value) {
    FM$VH$x.set(ms, value);
    return this;
  }

  public int y() {
    return (int) FM$VH$y.get(ms);
  }

  public StructAFM y(int value) {
    FM$VH$y.set(ms, value);
    return this;
  }
}

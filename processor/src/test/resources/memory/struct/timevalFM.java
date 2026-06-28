package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class timevalFM implements timeval {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
            ValueLayout.JAVA_INT.withName("tv_sec"),
            ValueLayout.JAVA_INT.withName("tv_usec"),
          }));

  public static final MemoryLayout.PathElement FM$PE$tv_sec =
      MemoryLayout.PathElement.groupElement("tv_sec");

  public static final MemoryLayout.PathElement FM$PE$tv_usec =
      MemoryLayout.PathElement.groupElement("tv_usec");

  public static final java.lang.invoke.VarHandle FM$VH$tv_sec =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$tv_sec), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$tv_usec =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$tv_usec), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
        FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static timevalFM reinterpret(MemorySegment ms) {
    return new timevalFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public timevalFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public timevalFM(MemorySegment ms) {
    this.ms = ms;
  }

  public int tv_sec() {
    return (int) FM$VH$tv_sec.get(ms);
  }

  public timevalFM tv_sec(int value) {
    FM$VH$tv_sec.set(ms, value);
    return this;
  }

  public int tv_usec() {
    return (int) FM$VH$tv_usec.get(ms);
  }

  public timevalFM tv_usec(int value) {
    FM$VH$tv_usec.set(ms, value);
    return this;
  }
}

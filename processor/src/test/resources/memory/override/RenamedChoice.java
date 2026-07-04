package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class RenamedChoice implements SimpleChoice {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.unionLayout(
          org.alveolo.ffm.ForeignUtils.unionPad(new MemoryLayout [] {
        ValueLayout.JAVA_INT.withName("i"),
        ValueLayout.JAVA_FLOAT.withName("f"),
      }));

  public static final MemoryLayout.PathElement FM$PE$i =
      MemoryLayout.PathElement.groupElement("i");

  public static final MemoryLayout.PathElement FM$PE$f =
      MemoryLayout.PathElement.groupElement("f");

  public static final java.lang.invoke.VarHandle FM$VH$i =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$i), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$f =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$f), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static RenamedChoice reinterpret(MemorySegment ms) {
    return new RenamedChoice(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public RenamedChoice(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public RenamedChoice(MemorySegment ms) {
    this.ms = ms;
  }

  public int i() {
    return (int) FM$VH$i.get(ms);
  }

  public RenamedChoice i(int value) {
    FM$VH$i.set(ms, value);
    return this;
  }

  public float f() {
    return (float) FM$VH$f.get(ms);
  }

  public RenamedChoice f(float value) {
    FM$VH$f.set(ms, value);
    return this;
  }
}

package pkg;
import java.lang.foreign.*;
@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class Union {
  public final MemorySegment ms;
  public Union(MemorySegment ms) {
    this.ms = ms;
  }

  public static final MemoryLayout FM$LAYOUT =
    MemoryLayout.unionLayout(
      org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
        ValueLayout.JAVA_INT.withName("i"),
        ValueLayout.JAVA_DOUBLE.withName("d"),
      }));
  public static Union allocate(SegmentAllocator allocator) {
    return new Union(allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment()));
  }

  private static final MemoryLayout.PathElement FM$PE$i =
    MemoryLayout.PathElement.groupElement("i");
  public static final java.lang.invoke.VarHandle FM$VH$i =
    FM$LAYOUT.varHandle(FM$PE$i);
  public int i() {
    return (int) FM$VH$i.get(ms);
  }
  public void i(int value) {
    FM$VH$i.set(ms, value);
  }

  private static final MemoryLayout.PathElement FM$PE$d =
    MemoryLayout.PathElement.groupElement("d");
  public static final java.lang.invoke.VarHandle FM$VH$d =
    FM$LAYOUT.varHandle(FM$PE$d);
  public double d() {
    return (double) FM$VH$d.get(ms);
  }
  public void d(double value) {
    FM$VH$d.set(ms, value);
  }
}

package pkg;
import java.lang.foreign.*;
@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class Struct2 {
  public final MemorySegment ms;
  public Struct2(MemorySegment ms) {
    this.ms = ms;
  }

  public static final MemoryLayout FM$LAYOUT =
    MemoryLayout.structLayout(
      org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
        ValueLayout.JAVA_LONG.withName("a"),
      }));
  public static Struct2 allocate(SegmentAllocator allocator) {
    return new Struct2(allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment()));
  }

  private static final MemoryLayout.PathElement FM$PE$a =
    MemoryLayout.PathElement.groupElement("a");
  public static final java.lang.invoke.VarHandle FM$VH$a =
    FM$LAYOUT.varHandle(FM$PE$a);
  public long a() {
    return (long) FM$VH$a.get(ms);
  }
  public void a(long value) {
    FM$VH$a.set(ms, value);
  }
}

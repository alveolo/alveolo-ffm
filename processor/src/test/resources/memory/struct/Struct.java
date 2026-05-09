package pkg;
import java.lang.foreign.*;
@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class Struct {
  public final MemorySegment ms;
  public Struct(MemorySegment ms) {
    this.ms = ms;
  }

  public static final MemoryLayout FM$LAYOUT =
    MemoryLayout.structLayout(
      org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
        ValueLayout.JAVA_INT.withName("x"),
        ValueLayout.JAVA_INT.withName("y"),
        MemoryLayout.sequenceLayout(5, ValueLayout.JAVA_INT)
            .withName("s"),
      }));
  public static Struct allocate(SegmentAllocator allocator) {
    return new Struct(allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment()));
  }

  private static final MemoryLayout.PathElement FM$PE$x =
    MemoryLayout.PathElement.groupElement("x");
  public static final java.lang.invoke.VarHandle FM$VH$x =
    FM$LAYOUT.varHandle(FM$PE$x);
  public int x() {
    return (int) FM$VH$x.get(ms);
  }
  public void x(int value) {
    FM$VH$x.set(ms, value);
  }

  private static final MemoryLayout.PathElement FM$PE$y =
    MemoryLayout.PathElement.groupElement("y");
  public static final java.lang.invoke.VarHandle FM$VH$y =
    FM$LAYOUT.varHandle(FM$PE$y);
  public int y() {
    return (int) FM$VH$y.get(ms);
  }
  public void y(int value) {
    FM$VH$y.set(ms, value);
  }

  private static final MemoryLayout.PathElement FM$PE$s =
      MemoryLayout.PathElement.groupElement("s");
  public static final long FM$OFFSET$s =
      FM$LAYOUT.byteOffset(FM$PE$s);
  public static final long FM$SIZE$s =
      FM$LAYOUT.select(FM$PE$s).byteSize();
  private MemorySegment FM$MS$s;
  public MemorySegment s$MemorySegment() {
    if (FM$MS$s == null) {
      FM$MS$s = ms.asSlice(FM$OFFSET$s, FM$SIZE$s);
    }
    return FM$MS$s;
  }
  private java.nio.IntBuffer FM$BB$s;
  public java.nio.IntBuffer s() {
    if (FM$BB$s == null) {
      FM$BB$s = s$MemorySegment().asByteBuffer().asIntBuffer();
    }
    return FM$BB$s;
  }
  public int s(int index) {
    return s$MemorySegment().getAtIndex(ValueLayout.JAVA_INT, index);
  }
  public void s(int index, int value) {
    s$MemorySegment().setAtIndex(ValueLayout.JAVA_INT, index, value);
  }
  public void s(int[] value) {
    if (value.length != 5) {
      throw new IllegalArgumentException();
    }
    MemorySegment.copy(value, 0,
        s$MemorySegment(), ValueLayout.JAVA_INT, 0, 5);
  }
}

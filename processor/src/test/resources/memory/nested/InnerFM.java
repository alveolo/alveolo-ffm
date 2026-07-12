package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class InnerFM implements Inner {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        ValueLayout.JAVA_INT.withName("a"),
        ValueLayout.JAVA_INT.withName("b"),
      }));

  public static final MemoryLayout.PathElement FM$PE$a =
      MemoryLayout.PathElement.groupElement("a");

  public static final MemoryLayout.PathElement FM$PE$b =
      MemoryLayout.PathElement.groupElement("b");

  public static final java.lang.invoke.VarHandle FM$VH$a =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$a), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$b =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$b), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static MemorySegment allocate(
      SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static InnerFM reinterpret(MemorySegment ms) {
    return new InnerFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static MemorySegment reinterpret(
      MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static MemorySegment FM$at(MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static InnerFM at(MemorySegment array, long index) {
    return new InnerFM(FM$at(array, index));
  }

  public final MemorySegment ms;

  public InnerFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public InnerFM(MemorySegment ms) {
    this.ms = ms;
  }

  public int a() {
    return (int) FM$VH$a.get(ms);
  }

  public InnerFM a(int value) {
    FM$VH$a.set(ms, value);
    return this;
  }

  public int b() {
    return (int) FM$VH$b.get(ms);
  }

  public InnerFM b(int value) {
    FM$VH$b.set(ms, value);
    return this;
  }
}

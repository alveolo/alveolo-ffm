package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public class BaseFM implements Base {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_SHORT.withName("base"),
      }));

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(MemoryLayout$F, count);
  }

  public static BaseFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new BaseFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return memorySegment.reinterpret(Math.multiplyExact(
        MemoryLayout$F.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment elementAt$F(
      java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, MemoryLayout$F.byteSize()), MemoryLayout$F.byteSize());
  }

  public static BaseFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new BaseFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public BaseFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public BaseFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      base$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("base");

  public static final java.lang.invoke.VarHandle base$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(base$PathElement$F), 1, 0L);

  public short base() {
    return (short) base$VarHandle$F.get(MemorySegment$F);
  }

  public BaseFM base(short value) {
    base$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }
}

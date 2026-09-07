package test;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public class BinaryDerivedFM implements BinaryDerived {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_LONG.withName("second"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("first"),
        java.lang.foreign.ValueLayout.JAVA_BYTE.withName("own"),
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

  public static BinaryDerivedFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : new BinaryDerivedFM(
        memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static BinaryDerivedFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new BinaryDerivedFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public BinaryDerivedFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public BinaryDerivedFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      second$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("second");

  public static final java.lang.invoke.VarHandle second$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(second$PathElement$F), 1, 0L);

  public long second() {
    return (long) second$VarHandle$F.get(MemorySegment$F);
  }

  public BinaryDerivedFM second(long value) {
    second$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      first$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("first");

  public static final java.lang.invoke.VarHandle first$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(first$PathElement$F), 1, 0L);

  public int first() {
    return (int) first$VarHandle$F.get(MemorySegment$F);
  }

  public BinaryDerivedFM first(int value) {
    first$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      own$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("own");

  public static final java.lang.invoke.VarHandle own$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(own$PathElement$F), 1, 0L);

  public byte own() {
    return (byte) own$VarHandle$F.get(MemorySegment$F);
  }

  public BinaryDerivedFM own(byte value) {
    own$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }
}

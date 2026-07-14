package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class IntBoxFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("value"),
      }));

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator$f) {
    return allocator$f.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator$f, long count$f) {
    if (count$f < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator$f.allocate(MemoryLayout$F, count$f);
  }

  public static IntBox reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return fromMemorySegment$F(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f, long count$f) {
    if (count$f < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return memorySegment$f.reinterpret(Math.multiplyExact(
        MemoryLayout$F.byteSize(), count$f));
  }

  private static java.lang.foreign.MemorySegment elementAt$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    if (index$f < 0) {
      throw new IndexOutOfBoundsException(index$f);
    }
    return array$f.asSlice(Math.multiplyExact(
        index$f, MemoryLayout$F.byteSize()), MemoryLayout$F.byteSize());
  }

  public static IntBox at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return fromMemorySegment$F(elementAt$F(array$f, index$f));
  }

  public static void toMemorySegment$F(
      IntBox source$f,
      java.lang.foreign.MemorySegment memorySegment$f,
      java.lang.foreign.SegmentAllocator allocator$f) {
    value(memorySegment$f, allocator$f, source$f.value());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator$f,
      IntBox source$f) {
    var memorySegment$f = allocate$F(allocator$f);
    toMemorySegment$F(source$f, memorySegment$f, allocator$f);
    return memorySegment$f;
  }

  public static IntBox fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new IntBox(
        value(memorySegment$f));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      value$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("value");

  public static final java.lang.invoke.VarHandle value$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(value$PathElement$F), 1, 0L);

  public static int value(java.lang.foreign.MemorySegment memorySegment$f) {
    return ((java.lang.foreign.MemorySegment) value$VarHandle$F.get(memorySegment$f)).reinterpret(java.lang.foreign.ValueLayout.JAVA_INT.byteSize())
        .get(java.lang.foreign.ValueLayout.JAVA_INT, 0L);
  }

  public static void value(
      java.lang.foreign.MemorySegment memorySegment$f, java.lang.foreign.SegmentAllocator allocator$f, int value$f) {
    var address$f = allocator$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT);
    address$f.set(java.lang.foreign.ValueLayout.JAVA_INT, 0L, value$f);
    value$VarHandle$F.set(memorySegment$f, address$f);
  }
}

package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class div_tFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("quot"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("rem"),
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

  public static div_t reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return fromMemorySegment$F(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static div_t at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      div_t source,
      java.lang.foreign.MemorySegment memorySegment) {
    quot(memorySegment, source.quot());
    rem(memorySegment, source.rem());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      div_t source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment);
    return memorySegment;
  }

  public static div_t fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new div_t(
        quot(memorySegment),
        rem(memorySegment));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      quot$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("quot");

  public static final java.lang.invoke.VarHandle quot$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(quot$PathElement$F), 1, 0L);

  public static int quot(java.lang.foreign.MemorySegment memorySegment) {
    return (int) quot$VarHandle$F.get(memorySegment);
  }

  public static void quot(java.lang.foreign.MemorySegment memorySegment, int value) {
    quot$VarHandle$F.set(memorySegment, value);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      rem$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("rem");

  public static final java.lang.invoke.VarHandle rem$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(rem$PathElement$F), 1, 0L);

  public static int rem(java.lang.foreign.MemorySegment memorySegment) {
    return (int) rem$VarHandle$F.get(memorySegment);
  }

  public static void rem(java.lang.foreign.MemorySegment memorySegment, int value) {
    rem$VarHandle$F.set(memorySegment, value);
  }
}

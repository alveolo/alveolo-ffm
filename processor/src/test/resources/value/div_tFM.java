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

  public static div_t reinterpret$F(
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

  public static div_t at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return fromMemorySegment$F(elementAt$F(array$f, index$f));
  }

  public static void toMemorySegment$F(
      div_t source$f,
      java.lang.foreign.MemorySegment memorySegment$f) {
    quot(memorySegment$f, source$f.quot());
    rem(memorySegment$f, source$f.rem());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator$f,
      div_t source$f) {
    var memorySegment$f = allocate$F(allocator$f);
    toMemorySegment$F(source$f, memorySegment$f);
    return memorySegment$f;
  }

  public static div_t fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new div_t(
        quot(memorySegment$f),
        rem(memorySegment$f));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      quot$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("quot");

  public static final java.lang.invoke.VarHandle quot$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(quot$PathElement$F), 1, 0L);

  public static int quot(java.lang.foreign.MemorySegment memorySegment$f) {
    return (int) quot$VarHandle$F.get(memorySegment$f);
  }

  public static void quot(java.lang.foreign.MemorySegment memorySegment$f, int value$f) {
    quot$VarHandle$F.set(memorySegment$f, value$f);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      rem$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("rem");

  public static final java.lang.invoke.VarHandle rem$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(rem$PathElement$F), 1, 0L);

  public static int rem(java.lang.foreign.MemorySegment memorySegment$f) {
    return (int) rem$VarHandle$F.get(memorySegment$f);
  }

  public static void rem(java.lang.foreign.MemorySegment memorySegment$f, int value$f) {
    rem$VarHandle$F.set(memorySegment$f, value$f);
  }
}

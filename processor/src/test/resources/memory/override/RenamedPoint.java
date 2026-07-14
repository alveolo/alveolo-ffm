package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class RenamedPoint {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("x"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("y"),
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

  public static SimpleOverrides reinterpret$F(
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

  public static SimpleOverrides at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return fromMemorySegment$F(elementAt$F(array$f, index$f));
  }

  public static void toMemorySegment$F(
      SimpleOverrides source$f,
      java.lang.foreign.MemorySegment memorySegment$f) {
    x(memorySegment$f, source$f.x());
    y(memorySegment$f, source$f.y());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator$f,
      SimpleOverrides source$f) {
    var memorySegment$f = allocate$F(allocator$f);
    toMemorySegment$F(source$f, memorySegment$f);
    return memorySegment$f;
  }

  public static SimpleOverrides fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new SimpleOverrides(
        x(memorySegment$f),
        y(memorySegment$f));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      x$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("x");

  public static final java.lang.invoke.VarHandle x$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(x$PathElement$F), 1, 0L);

  public static int x(java.lang.foreign.MemorySegment memorySegment$f) {
    return (int) x$VarHandle$F.get(memorySegment$f);
  }

  public static void x(java.lang.foreign.MemorySegment memorySegment$f, int value$f) {
    x$VarHandle$F.set(memorySegment$f, value$f);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      y$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("y");

  public static final java.lang.invoke.VarHandle y$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(y$PathElement$F), 1, 0L);

  public static int y(java.lang.foreign.MemorySegment memorySegment$f) {
    return (int) y$VarHandle$F.get(memorySegment$f);
  }

  public static void y(java.lang.foreign.MemorySegment memorySegment$f, int value$f) {
    y$VarHandle$F.set(memorySegment$f, value$f);
  }
}

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

  public static SimpleOverrides reinterpret$F(
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

  public static SimpleOverrides at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      SimpleOverrides source,
      java.lang.foreign.MemorySegment memorySegment) {
    x(memorySegment, source.x());
    y(memorySegment, source.y());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      SimpleOverrides source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment);
    return memorySegment;
  }

  public static SimpleOverrides fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new SimpleOverrides(
        x(memorySegment),
        y(memorySegment));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      x$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("x");

  public static final java.lang.invoke.VarHandle x$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(x$PathElement$F), 1, 0L);

  public static int x(java.lang.foreign.MemorySegment memorySegment) {
    return (int) x$VarHandle$F.get(memorySegment);
  }

  public static void x(java.lang.foreign.MemorySegment memorySegment, int value) {
    x$VarHandle$F.set(memorySegment, value);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      y$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("y");

  public static final java.lang.invoke.VarHandle y$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(y$PathElement$F), 1, 0L);

  public static int y(java.lang.foreign.MemorySegment memorySegment) {
    return (int) y$VarHandle$F.get(memorySegment);
  }

  public static void y(java.lang.foreign.MemorySegment memorySegment, int value) {
    y$VarHandle$F.set(memorySegment, value);
  }
}

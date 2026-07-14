package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class StructBFM implements StructB {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_BOOLEAN.withName("b"),
        java.lang.foreign.ValueLayout.JAVA_CHAR.withName("c"),
        java.lang.foreign.ValueLayout.JAVA_SHORT.withName("s"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("i"),
        java.lang.foreign.ValueLayout.JAVA_LONG.withName("l"),
        java.lang.foreign.ValueLayout.JAVA_FLOAT.withName("f"),
        java.lang.foreign.ValueLayout.JAVA_DOUBLE.withName("d"),
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

  public static StructBFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new StructBFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static StructBFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new StructBFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public StructBFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public StructBFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      b$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("b");

  public static final java.lang.invoke.VarHandle b$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(b$PathElement$F), 1, 0L);

  public boolean b() {
    return (boolean) b$VarHandle$F.get(MemorySegment$F);
  }

  public StructBFM b(boolean value$f) {
    b$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      c$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("c");

  public static final java.lang.invoke.VarHandle c$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(c$PathElement$F), 1, 0L);

  public char c() {
    return (char) c$VarHandle$F.get(MemorySegment$F);
  }

  public StructBFM c(char value$f) {
    c$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      s$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("s");

  public static final java.lang.invoke.VarHandle s$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(s$PathElement$F), 1, 0L);

  public short s() {
    return (short) s$VarHandle$F.get(MemorySegment$F);
  }

  public StructBFM s(short value$f) {
    s$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      i$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("i");

  public static final java.lang.invoke.VarHandle i$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(i$PathElement$F), 1, 0L);

  public int i() {
    return (int) i$VarHandle$F.get(MemorySegment$F);
  }

  public StructBFM i(int value$f) {
    i$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      l$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("l");

  public static final java.lang.invoke.VarHandle l$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(l$PathElement$F), 1, 0L);

  public long l() {
    return (long) l$VarHandle$F.get(MemorySegment$F);
  }

  public StructBFM l(long value$f) {
    l$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      f$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("f");

  public static final java.lang.invoke.VarHandle f$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(f$PathElement$F), 1, 0L);

  public float f() {
    return (float) f$VarHandle$F.get(MemorySegment$F);
  }

  public StructBFM f(float value$f) {
    f$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      d$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("d");

  public static final java.lang.invoke.VarHandle d$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(d$PathElement$F), 1, 0L);

  public double d() {
    return (double) d$VarHandle$F.get(MemorySegment$F);
  }

  public StructBFM d(double value$f) {
    d$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }
}

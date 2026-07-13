package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class StructBFM implements StructB {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_BOOLEAN.withName("b"),
        java.lang.foreign.ValueLayout.JAVA_CHAR.withName("c"),
        java.lang.foreign.ValueLayout.JAVA_SHORT.withName("s"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("i"),
        java.lang.foreign.ValueLayout.JAVA_LONG.withName("l"),
        java.lang.foreign.ValueLayout.JAVA_FLOAT.withName("f"),
        java.lang.foreign.ValueLayout.JAVA_DOUBLE.withName("d"),
      }));

  public static java.lang.foreign.MemorySegment allocate(java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static StructBFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new StructBFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret(
      java.lang.foreign.MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment FM$at(java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static StructBFM at(java.lang.foreign.MemorySegment array, long index) {
    return new StructBFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public StructBFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public StructBFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$b =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("b");

  public static final java.lang.invoke.VarHandle FM$VH$b =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$b), 1, 0L);

  public boolean b() {
    return (boolean) FM$VH$b.get(ms);
  }

  public StructBFM b(boolean value) {
    FM$VH$b.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$c =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("c");

  public static final java.lang.invoke.VarHandle FM$VH$c =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$c), 1, 0L);

  public char c() {
    return (char) FM$VH$c.get(ms);
  }

  public StructBFM c(char value) {
    FM$VH$c.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$s =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("s");

  public static final java.lang.invoke.VarHandle FM$VH$s =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$s), 1, 0L);

  public short s() {
    return (short) FM$VH$s.get(ms);
  }

  public StructBFM s(short value) {
    FM$VH$s.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$i =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("i");

  public static final java.lang.invoke.VarHandle FM$VH$i =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$i), 1, 0L);

  public int i() {
    return (int) FM$VH$i.get(ms);
  }

  public StructBFM i(int value) {
    FM$VH$i.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$l =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("l");

  public static final java.lang.invoke.VarHandle FM$VH$l =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$l), 1, 0L);

  public long l() {
    return (long) FM$VH$l.get(ms);
  }

  public StructBFM l(long value) {
    FM$VH$l.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$f =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("f");

  public static final java.lang.invoke.VarHandle FM$VH$f =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$f), 1, 0L);

  public float f() {
    return (float) FM$VH$f.get(ms);
  }

  public StructBFM f(float value) {
    FM$VH$f.set(ms, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$d =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("d");

  public static final java.lang.invoke.VarHandle FM$VH$d =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$d), 1, 0L);

  public double d() {
    return (double) FM$VH$d.get(ms);
  }

  public StructBFM d(double value) {
    FM$VH$d.set(ms, value);
    return this;
  }
}

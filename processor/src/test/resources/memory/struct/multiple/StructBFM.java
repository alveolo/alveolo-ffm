package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class StructBFM implements StructB {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
            ValueLayout.JAVA_BOOLEAN.withName("b"),
            ValueLayout.JAVA_CHAR.withName("c"),
            ValueLayout.JAVA_SHORT.withName("s"),
            ValueLayout.JAVA_INT.withName("i"),
            ValueLayout.JAVA_LONG.withName("l"),
            ValueLayout.JAVA_FLOAT.withName("f"),
            ValueLayout.JAVA_DOUBLE.withName("d"),
          }));

  public static final MemoryLayout.PathElement FM$PE$b =
      MemoryLayout.PathElement.groupElement("b");

  public static final MemoryLayout.PathElement FM$PE$c =
      MemoryLayout.PathElement.groupElement("c");

  public static final MemoryLayout.PathElement FM$PE$s =
      MemoryLayout.PathElement.groupElement("s");

  public static final MemoryLayout.PathElement FM$PE$i =
      MemoryLayout.PathElement.groupElement("i");

  public static final MemoryLayout.PathElement FM$PE$l =
      MemoryLayout.PathElement.groupElement("l");

  public static final MemoryLayout.PathElement FM$PE$f =
      MemoryLayout.PathElement.groupElement("f");

  public static final MemoryLayout.PathElement FM$PE$d =
      MemoryLayout.PathElement.groupElement("d");

  public static final java.lang.invoke.VarHandle FM$VH$b =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$b), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$c =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$c), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$s =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$s), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$i =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$i), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$l =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$l), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$f =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$f), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$d =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$d), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static StructBFM reinterpret(MemorySegment ms) {
    return new StructBFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public StructBFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public StructBFM(MemorySegment ms) {
    this.ms = ms;
  }

  public boolean b() {
    return (boolean) FM$VH$b.get(ms);
  }

  public StructBFM b(boolean value) {
    FM$VH$b.set(ms, value);
    return this;
  }

  public char c() {
    return (char) FM$VH$c.get(ms);
  }

  public StructBFM c(char value) {
    FM$VH$c.set(ms, value);
    return this;
  }

  public short s() {
    return (short) FM$VH$s.get(ms);
  }

  public StructBFM s(short value) {
    FM$VH$s.set(ms, value);
    return this;
  }

  public int i() {
    return (int) FM$VH$i.get(ms);
  }

  public StructBFM i(int value) {
    FM$VH$i.set(ms, value);
    return this;
  }

  public long l() {
    return (long) FM$VH$l.get(ms);
  }

  public StructBFM l(long value) {
    FM$VH$l.set(ms, value);
    return this;
  }

  public float f() {
    return (float) FM$VH$f.get(ms);
  }

  public StructBFM f(float value) {
    FM$VH$f.set(ms, value);
    return this;
  }

  public double d() {
    return (double) FM$VH$d.get(ms);
  }

  public StructBFM d(double value) {
    FM$VH$d.set(ms, value);
    return this;
  }
}

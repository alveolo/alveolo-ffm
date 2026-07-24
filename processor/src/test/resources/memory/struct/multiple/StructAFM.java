package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class StructAFM implements StructA {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("x"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("y"),
        org.alveolo.ffm.NativeTypes.C_LONG_LAYOUT.withName("signed"),
        org.alveolo.ffm.NativeTypes.C_LONG_LAYOUT.withName("unsigned"),
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

  public static StructAFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new StructAFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static StructAFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new StructAFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public StructAFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public StructAFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      x$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("x");

  public static final java.lang.invoke.VarHandle x$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(x$PathElement$F), 1, 0L);

  public int x() {
    return (int) x$VarHandle$F.get(MemorySegment$F);
  }

  public StructAFM x(int value) {
    x$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      y$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("y");

  public static final java.lang.invoke.VarHandle y$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(y$PathElement$F), 1, 0L);

  public int y() {
    return (int) y$VarHandle$F.get(MemorySegment$F);
  }

  public StructAFM y(int value) {
    y$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      signed$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("signed");

  public static final java.lang.invoke.VarHandle signed$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(signed$PathElement$F), 1, 0L);

  public static final java.lang.invoke.MethodHandle signed$get$F =
      org.alveolo.ffm.NativeTypes.adaptGetter(
          signed$VarHandle$F, org.alveolo.ffm.NativeTypes.Type.SLONG);

  public static final java.lang.invoke.MethodHandle signed$set$F =
      org.alveolo.ffm.NativeTypes.adaptSetter(
          signed$VarHandle$F, org.alveolo.ffm.NativeTypes.Type.SLONG);

  public long signed() {
    try {
      return (long) signed$get$F.invokeExact(MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public StructAFM signed(long value) {
    try {
      signed$set$F.invokeExact(MemorySegment$F, value);
      return this;
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      unsigned$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("unsigned");

  public static final java.lang.invoke.VarHandle unsigned$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(unsigned$PathElement$F), 1, 0L);

  public static final java.lang.invoke.MethodHandle unsigned$get$F =
      org.alveolo.ffm.NativeTypes.adaptGetter(
          unsigned$VarHandle$F, org.alveolo.ffm.NativeTypes.Type.ULONG);

  public static final java.lang.invoke.MethodHandle unsigned$set$F =
      org.alveolo.ffm.NativeTypes.adaptSetter(
          unsigned$VarHandle$F, org.alveolo.ffm.NativeTypes.Type.ULONG);

  public long unsigned() {
    try {
      return (long) unsigned$get$F.invokeExact(MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public StructAFM unsigned(long value) {
    try {
      unsigned$set$F.invokeExact(MemorySegment$F, value);
      return this;
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

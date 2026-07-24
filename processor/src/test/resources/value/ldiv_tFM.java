package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ldiv_tFM implements ldiv_t {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        org.alveolo.ffm.NativeTypes.C_LONG_LAYOUT.withName("quot"),
        org.alveolo.ffm.NativeTypes.C_LONG_LAYOUT.withName("rem"),
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

  public static ldiv_tFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new ldiv_tFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static ldiv_tFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new ldiv_tFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public ldiv_tFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public ldiv_tFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      quot$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("quot");

  public static final java.lang.invoke.VarHandle quot$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(quot$PathElement$F), 1, 0L);

  public static final java.lang.invoke.MethodHandle quot$get$F =
      org.alveolo.ffm.NativeTypes.adaptGetter(
          quot$VarHandle$F, org.alveolo.ffm.NativeTypes.Type.SLONG);

  public static final java.lang.invoke.MethodHandle quot$set$F =
      org.alveolo.ffm.NativeTypes.adaptSetter(
          quot$VarHandle$F, org.alveolo.ffm.NativeTypes.Type.SLONG);

  public long quot() {
    try {
      return (long) quot$get$F.invokeExact(MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public ldiv_tFM quot(long value) {
    try {
      quot$set$F.invokeExact(MemorySegment$F, value);
      return this;
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      rem$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("rem");

  public static final java.lang.invoke.VarHandle rem$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(rem$PathElement$F), 1, 0L);

  public static final java.lang.invoke.MethodHandle rem$get$F =
      org.alveolo.ffm.NativeTypes.adaptGetter(
          rem$VarHandle$F, org.alveolo.ffm.NativeTypes.Type.SLONG);

  public static final java.lang.invoke.MethodHandle rem$set$F =
      org.alveolo.ffm.NativeTypes.adaptSetter(
          rem$VarHandle$F, org.alveolo.ffm.NativeTypes.Type.SLONG);

  public long rem() {
    try {
      return (long) rem$get$F.invokeExact(MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public ldiv_tFM rem(long value) {
    try {
      rem$set$F.invokeExact(MemorySegment$F, value);
      return this;
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

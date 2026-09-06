package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class SizeValueFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        org.alveolo.ffm.NativeType.SIZE_T.layout.withName("value"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("pointer"),
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

  public static SizeValue reinterpret$F(
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

  public static SizeValue at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      SizeValue source,
      java.lang.foreign.MemorySegment memorySegment,
      java.lang.foreign.SegmentAllocator allocator) {
    value(memorySegment, source.value());
    pointer(memorySegment, allocator, source.pointer());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      SizeValue source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment, allocator);
    return memorySegment;
  }

  public static SizeValue fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new SizeValue(
        value(memorySegment),
        pointer(memorySegment));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      value$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("value");

  public static final java.lang.invoke.VarHandle value$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(value$PathElement$F), 1, 0L);

  public static final java.lang.invoke.MethodHandle value$get$F =
      org.alveolo.ffm.NativeType.SIZE_T.adaptGetter(value$VarHandle$F);

  public static final java.lang.invoke.MethodHandle value$set$F =
      org.alveolo.ffm.NativeType.SIZE_T.adaptSetter(value$VarHandle$F);

  public static long value(java.lang.foreign.MemorySegment memorySegment) {
    try {
      return (long) value$get$F.invokeExact(memorySegment);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public static void value(java.lang.foreign.MemorySegment memorySegment, long value) {
    try {
      value$set$F.invokeExact(memorySegment, value);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      pointer$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("pointer");

  public static final java.lang.invoke.VarHandle pointer$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(pointer$PathElement$F), 1, 0L);

  public static long pointer(java.lang.foreign.MemorySegment memorySegment) {
    return org.alveolo.ffm.NativeType.getSizeT(((java.lang.foreign.MemorySegment) pointer$VarHandle$F.get(memorySegment)).reinterpret(org.alveolo.ffm.NativeType.SIZE_T.layout.byteSize()), 0L);
  }

  public static void pointer(
      java.lang.foreign.MemorySegment memorySegment, java.lang.foreign.SegmentAllocator allocator, long value) {
    var address = allocator.allocate(org.alveolo.ffm.NativeType.SIZE_T.layout);
    org.alveolo.ffm.NativeType.setSizeT(address, 0L, value);
    pointer$VarHandle$F.set(memorySegment, address);
  }
}

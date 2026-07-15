package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ObjFM implements Obj {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("field"),
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

  public static ObjFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new ObjFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static ObjFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new ObjFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public ObjFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public ObjFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      field$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("field");

  public static final java.lang.invoke.VarHandle field$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(field$PathElement$F), 1, 0L);

  public int field() {
    return (int) field$VarHandle$F.get(MemorySegment$F);
  }

  public ObjFM field(int value) {
    field$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  private static final java.lang.invoke.MethodHandle SymbolMethodHandle$0$F =
      pkg.NativeApiFFM.Linker$F.downcallHandle(
      pkg.NativeApiFFM.SymbolLookup$F.findOrThrow("native_symbol"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  private static final java.lang.invoke.MethodHandle SymbolMethodHandle$1$F =
      pkg.NativeApiFFM.Linker$F.downcallHandle(
      pkg.NativeApiFFM.SymbolLookup$F.findOrThrow("native_strlen"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int call(
      int arg) {
    try {
      return (int) SymbolMethodHandle$0$F.invokeExact(
          this.MemorySegment$F,
          arg);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  public long strlen(
      java.lang.String value) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (long) SymbolMethodHandle$1$F.invokeExact(
          this.MemorySegment$F,
          arena$f.allocateFrom(value));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

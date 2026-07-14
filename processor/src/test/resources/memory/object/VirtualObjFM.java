package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class VirtualObjFM implements VirtualObj {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("vtable$F"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("field"),
      }));

  public static final java.lang.foreign.MemoryLayout.PathElement
      vtable$F$PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .groupElement("vtable$F");

  public static final java.lang.invoke.VarHandle
      vtable$F$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(vtable$F$PathElement$F), 1, 0L);

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

  public static VirtualObjFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new VirtualObjFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static VirtualObjFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new VirtualObjFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  private final VirtualObjVtblFD Vtable$F;

  public VirtualObjFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public VirtualObjFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
    this.Vtable$F = VirtualObjVtblFD.reinterpret$F((java.lang.foreign.MemorySegment) vtable$F$VarHandle$F.get(MemorySegment$F));
  }

  private VirtualObjVtblFD Vtable$F() {
    return Vtable$F;
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

  public VirtualObjFM field(int value$f) {
    field$VarHandle$F.set(MemorySegment$F, value$f);
    return this;
  }

  private static final java.lang.invoke.MethodHandle SymbolMethodHandle$0$F =
      pkg.NativeApiFFM.Linker$F.downcallHandle(
      pkg.NativeApiFFM.SymbolLookup$F.findOrThrow("native_symbol"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public int method(
      int arg) {
    return Vtable$F().method(this, arg);
  }

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    return Vtable$F().sum(this, values);
  }

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
}

package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class VirtualObjFM implements VirtualObj {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("ff$vtbl"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("field"),
      }));

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$ff$vtbl =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("ff$vtbl");

  public static final java.lang.invoke.VarHandle FM$VH$ff$vtbl =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$ff$vtbl), 1, 0L);

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

  public static VirtualObjFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new VirtualObjFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static VirtualObjFM at(java.lang.foreign.MemorySegment array, long index) {
    return new VirtualObjFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  private final VirtualObjVtbl ff$vtbl;

  public VirtualObjFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public VirtualObjFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
    this.ff$vtbl = VirtualObjVtblFD.reinterpret((java.lang.foreign.MemorySegment) FM$VH$ff$vtbl.get(ms));
  }

  private VirtualObjVtbl ff$vtbl() {
    return ff$vtbl;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$field =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("field");

  public static final java.lang.invoke.VarHandle FM$VH$field =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$field), 1, 0L);

  public int field() {
    return (int) FM$VH$field.get(ms);
  }

  public VirtualObjFM field(int value) {
    FM$VH$field.set(ms, value);
    return this;
  }

  private static final class FF$SYMBOLS {

    private static final java.lang.invoke.MethodHandle FF$MH$0 = pkg.NativeApiFFM.FF$LINKER.downcallHandle(
        pkg.NativeApiFFM.FF$LOOKUP.findOrThrow("native_symbol"),
        java.lang.foreign.FunctionDescriptor.of(
            java.lang.foreign.ValueLayout.JAVA_INT,
            java.lang.foreign.ValueLayout.ADDRESS,
            java.lang.foreign.ValueLayout.JAVA_INT));
  }

  public int method(
      int arg) {
    return ff$vtbl().method(this, arg);
  }

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    return ff$vtbl().sum(this, values);
  }

  public int call(
      int arg) {
    try {
      return (int) FF$SYMBOLS.FF$MH$0.invokeExact(
          this.ms,
          arg);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

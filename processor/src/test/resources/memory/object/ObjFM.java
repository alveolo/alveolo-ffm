package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ObjFM implements Obj {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("field"),
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

  public static ObjFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new ObjFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static ObjFM at(java.lang.foreign.MemorySegment array, long index) {
    return new ObjFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public ObjFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public ObjFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$field =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("field");

  public static final java.lang.invoke.VarHandle FM$VH$field =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$field), 1, 0L);

  public int field() {
    return (int) FM$VH$field.get(ms);
  }

  public ObjFM field(int value) {
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

    private static final java.lang.invoke.MethodHandle FF$MH$1 = pkg.NativeApiFFM.FF$LINKER.downcallHandle(
        pkg.NativeApiFFM.FF$LOOKUP.findOrThrow("native_strlen"),
        java.lang.foreign.FunctionDescriptor.of(
            java.lang.foreign.ValueLayout.JAVA_LONG,
            java.lang.foreign.ValueLayout.ADDRESS,
            java.lang.foreign.ValueLayout.ADDRESS));
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

  public long strlen(
      java.lang.String value) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      return (long) FF$SYMBOLS.FF$MH$1.invokeExact(
          this.ms,
          ff$arena.allocateFrom(value));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

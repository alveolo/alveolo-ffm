package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class VirtualStructVtbl implements VirtualStructVtblSpec {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.sequenceLayout(2L,
          java.lang.foreign.ValueLayout.ADDRESS);

  public static VirtualStructVtbl reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new VirtualStructVtbl(memorySegment$f.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public VirtualStructVtbl(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
    this.MethodHandle$0$F = DowncallHandle$0$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 0L));
    this.MethodHandle$1$F = DowncallHandle$1$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 1L));
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$0$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS,
          passmode.CircularValue.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$0$F;

  public int generatedCircular(
      passmode.VirtualStructSpec self$f,
      passmode.CircularDefault defaultValue,
      passmode.CircularValue value,
      passmode.CircularAddress address) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          ((passmode.VirtualStruct)self$f).MemorySegment$F,
          defaultValue.MemorySegment$F,
          value.MemorySegment$F,
          address.MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$1$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          passmode.CircularAddress.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$1$F;

  public int generatedOverrides(
      passmode.VirtualStructSpec self$f,
      passmode.CircularAddress value,
      passmode.CircularValue address) {
    try {
      return (int) MethodHandle$1$F.invokeExact(
          ((passmode.VirtualStruct)self$f).MemorySegment$F,
          value.MemorySegment$F,
          address.MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

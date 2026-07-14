package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class RenamedVtblFD implements RenamedVtbl {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.sequenceLayout(1L,
          java.lang.foreign.ValueLayout.ADDRESS);

  public static RenamedVtblFD reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new RenamedVtblFD(memorySegment$f.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public RenamedVtblFD(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
    this.MethodHandle$0$F = DowncallHandle$0$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 0L));
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$0$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  private final java.lang.invoke.MethodHandle MethodHandle$0$F;

  public int call(
      int value) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          value);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

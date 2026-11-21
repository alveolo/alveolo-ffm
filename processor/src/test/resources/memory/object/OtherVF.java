package test;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
final class OtherVF implements OtherV {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.sequenceLayout(1L,
          java.lang.foreign.ValueLayout.ADDRESS);

  public static OtherVF reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new OtherVF(memorySegment.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public OtherVF(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
    this.MethodHandle$0$F = DowncallHandle$0$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 0L));
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$0$F =
      Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$0$F;

  public int call(
      test.NativeObject self$f) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          (java.lang.foreign.MemorySegment) (self$f == null ? java.lang.foreign.MemorySegment.NULL : ((test.Other) self$f).MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

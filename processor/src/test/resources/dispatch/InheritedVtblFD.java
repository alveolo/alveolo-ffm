package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class InheritedVtblFD implements InheritedVtbl {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.sequenceLayout(5L,
          java.lang.foreign.ValueLayout.ADDRESS);

  public static InheritedVtblFD reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new InheritedVtblFD(memorySegment.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public InheritedVtblFD(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
    this.MethodHandle$0$F = DowncallHandle$0$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 4L));
    this.MethodHandle$1$F = DowncallHandle$1$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 1L));
    this.MethodHandle$2$F = DowncallHandle$2$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 3L));
    this.MethodHandle$3$F = DowncallHandle$3$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 0L));
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$0$F =
      Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.of(
              org.alveolo.ffm.CanonicalLayout.SIZE_T,
              java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$0$F;

  public long length(
      java.lang.String value) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (long) MethodHandle$0$F.invokeExact(
          arena$f.allocateFrom(value));
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
              java.lang.foreign.ValueLayout.JAVA_INT,
              java.lang.foreign.ValueLayout.JAVA_INT));

  private final java.lang.invoke.MethodHandle MethodHandle$1$F;

  public int add(
      int left,
      int right) {
    try {
      return (int) MethodHandle$1$F.invokeExact(
          left,
          right);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$2$F =
      Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              java.lang.foreign.ValueLayout.JAVA_INT));

  private final java.lang.invoke.MethodHandle MethodHandle$2$F;

  public int shared(
      int value) {
    try {
      return (int) MethodHandle$2$F.invokeExact(
          value);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$3$F =
      Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.ofVoid(
              ));

  private final java.lang.invoke.MethodHandle MethodHandle$3$F;

  public void reset(
      ) {
    try {
      MethodHandle$3$F.invokeExact(
          );
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

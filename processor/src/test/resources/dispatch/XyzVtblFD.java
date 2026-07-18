package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class XyzVtblFD implements XyzVtbl {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.sequenceLayout(6L,
          java.lang.foreign.ValueLayout.ADDRESS);

  public static XyzVtblFD reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new XyzVtblFD(memorySegment.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public XyzVtblFD(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
    this.MethodHandle$0$F = DowncallHandle$0$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 1L));
    this.MethodHandle$1$F = DowncallHandle$1$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 3L));
    this.MethodHandle$2$F = DowncallHandle$2$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 2L));
    this.MethodHandle$3$F = DowncallHandle$3$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 0L));
    this.MethodHandle$4$F = DowncallHandle$4$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 4L));
    this.MethodHandle$5$F = DowncallHandle$5$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 5L));
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$0$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  private final java.lang.invoke.MethodHandle MethodHandle$0$F;

  public int add(
      int a,
      int b) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          a,
          b);
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

  public int sub(
      int a,
      int b) {
    try {
      return (int) MethodHandle$1$F.invokeExact(
          a,
          b);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$2$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$2$F;

  public long strlen(
      java.lang.String utf8z) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (long) MethodHandle$2$F.invokeExact(
          arena$f.allocateFrom(utf8z));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$3$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$3$F;

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      if (values$size$f != 3) {
        throw new IllegalArgumentException(
            "values length must be 3");
      }
      var values$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      java.lang.foreign.MemorySegment.copy(
          values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      return (int) MethodHandle$3$F.invokeExact(
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$4$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT),
          java.lang.foreign.Linker.Option.firstVariadicArg(1),
          pkg.NativeError.LinkerOption$F);

  private final java.lang.invoke.MethodHandle MethodHandle$4$F;

  public int capturedCall(
      pkg.NativeErrorSpec capture,
      int parameter) {
    try {
      return (int) MethodHandle$4$F.invokeExact(
          ((pkg.NativeError) capture).MemorySegment$F,
          parameter);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$5$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT),
          pkg.NativeError.LinkerOption$F);

  private final java.lang.invoke.MethodHandle MethodHandle$5$F;

  public int concreteCapturedCall(
      pkg.NativeError capture,
      int parameter) {
    try {
      return (int) MethodHandle$5$F.invokeExact(
          capture.MemorySegment$F,
          parameter);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

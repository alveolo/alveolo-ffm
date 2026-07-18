package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class LibCFFM implements LibC {
  public static final LibCFFM INSTANCE$F = new LibCFFM();

  private LibCFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("abs"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public int abs(
      int number) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          number);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("abs"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public int renamed(
      int number) {
    try {
      return (int) MethodHandle$1$F.invokeExact(
          number);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("div"),
      java.lang.foreign.FunctionDescriptor.of(
          pkg.div_tFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public pkg.div_t div(
      int numerator,
      int denominator) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return pkg.div_tFM.fromMemorySegment$F((java.lang.foreign.MemorySegment) MethodHandle$2$F.invokeExact(
          (java.lang.foreign.SegmentAllocator) arena$f,
          numerator,
          denominator));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$3$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("ldiv"),
      java.lang.foreign.FunctionDescriptor.of(
          pkg.ldiv_tFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.JAVA_LONG));

  public pkg.@org.alveolo.ffm.Value ldiv_t ldiv(
      java.lang.foreign.SegmentAllocator allocator,
      long numerator,
      long denominator) {
    try {
      return new pkg.ldiv_tFM((java.lang.foreign.MemorySegment) MethodHandle$3$F.invokeExact(
          allocator,
          numerator,
          denominator));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$4$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("strlen"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS));

  public long strlen(
      java.lang.String utf8z) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (long) MethodHandle$4$F.invokeExact(
          arena$f.allocateFrom(utf8z));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$5$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("l64a"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_LONG));

  public java.lang.String l64a(
      long n) {
    try {
      var stringResult$f = (java.lang.foreign.MemorySegment) MethodHandle$5$F.invokeExact(
          n);
      return stringResult$f.address() == 0L ? null
          : stringResult$f.reinterpret(Long.MAX_VALUE).getString(0L);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$6$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("fcntl"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT),
          java.lang.foreign.Linker.Option.firstVariadicArg(2));

  public int fcntl(
      int descriptor,
      int operation) {
    try {
      return (int) MethodHandle$6$F.invokeExact(
          descriptor,
          operation);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$7$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("fcntl"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT),
          java.lang.foreign.Linker.Option.firstVariadicArg(2));

  public int fcntl(
      int descriptor,
      int operation,
      int argument) {
    try {
      return (int) MethodHandle$7$F.invokeExact(
          descriptor,
          operation,
          argument);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

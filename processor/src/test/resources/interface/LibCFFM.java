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
      SymbolLookup$F.find("abs")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT)))
          .orElse(null);

  public int abs(
      int number) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: abs");
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
      SymbolLookup$F.find("abs")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT)))
          .orElse(null);

  public int renamed(
      int number) {
    if (MethodHandle$1$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: abs");
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
      SymbolLookup$F.find("div")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  pkg.div_tFM.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT)))
          .orElse(null);

  public pkg.div_t div(
      int numerator,
      int denominator) {
    if (MethodHandle$2$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: div");
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
      SymbolLookup$F.find("ldiv")
          .map(address$f -> org.alveolo.ffm.NativeType.adaptDowncall(
              Linker$F.downcallHandle(
                  address$f,
                  java.lang.foreign.FunctionDescriptor.of(
                      pkg.ldiv_tFM.MemoryLayout$F,
                      org.alveolo.ffm.NativeType.SLONG.layout,
                      org.alveolo.ffm.NativeType.SLONG.layout)),
              null,
              new org.alveolo.ffm.NativeType[] {
                  null,
                  org.alveolo.ffm.NativeType.SLONG,
                  org.alveolo.ffm.NativeType.SLONG
              }))
          .orElse(null);

  public pkg.@org.alveolo.ffm.Value ldiv_t ldiv(
      java.lang.foreign.SegmentAllocator allocator,
      long numerator,
      long denominator) {
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: ldiv");
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
      SymbolLookup$F.find("strlen")
          .map(address$f -> org.alveolo.ffm.NativeType.adaptDowncall(
              Linker$F.downcallHandle(
                  address$f,
                  java.lang.foreign.FunctionDescriptor.of(
                      org.alveolo.ffm.NativeType.SIZE_T.layout,
                      java.lang.foreign.ValueLayout.ADDRESS)),
              org.alveolo.ffm.NativeType.SIZE_T,
              new org.alveolo.ffm.NativeType[] {
                  null
              }))
          .orElse(null);

  public long strlen(
      java.lang.String utf8z) {
    if (MethodHandle$4$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: strlen");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (long) MethodHandle$4$F.invokeExact(
          (java.lang.foreign.MemorySegment) (utf8z == null ? java.lang.foreign.MemorySegment.NULL : arena$f.allocateFrom(utf8z)));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$5$F =
      SymbolLookup$F.find("l64a")
          .map(address$f -> org.alveolo.ffm.NativeType.adaptDowncall(
              Linker$F.downcallHandle(
                  address$f,
                  java.lang.foreign.FunctionDescriptor.of(
                      java.lang.foreign.ValueLayout.ADDRESS,
                      org.alveolo.ffm.NativeType.SLONG.layout)),
              null,
              new org.alveolo.ffm.NativeType[] {
                  org.alveolo.ffm.NativeType.SLONG
              }))
          .orElse(null);

  public java.lang.String l64a(
      long n) {
    if (MethodHandle$5$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: l64a");
    try {
      var stringResult$f = (java.lang.foreign.MemorySegment) MethodHandle$5$F.invokeExact(
          n);
      return stringResult$f.equals(java.lang.foreign.MemorySegment.NULL) ? null
          : stringResult$f.reinterpret(Long.MAX_VALUE).getString(0L);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$6$F =
      SymbolLookup$F.find("fcntl")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT),
              java.lang.foreign.Linker.Option.firstVariadicArg(2)))
          .orElse(null);

  public int fcntl(
      int descriptor,
      int operation) {
    if (MethodHandle$6$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: fcntl");
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
      SymbolLookup$F.find("fcntl")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT),
              java.lang.foreign.Linker.Option.firstVariadicArg(2)))
          .orElse(null);

  public int fcntl(
      int descriptor,
      int operation,
      int argument) {
    if (MethodHandle$7$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: fcntl");
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

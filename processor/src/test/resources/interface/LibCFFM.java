package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class LibCFFM implements LibC {
  public static final LibCFFM INSTANCE = new LibCFFM();

  private LibCFFM() {}

  public static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();

  private static final java.lang.invoke.MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("abs"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public int abs(
      int number) {
    try {
      return (int) FF$MH$0.invokeExact(
          number);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$1 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("abs"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public int renamed(
      int number) {
    try {
      return (int) FF$MH$1.invokeExact(
          number);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$2 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("div"),
      java.lang.foreign.FunctionDescriptor.of(
          pkg.div_tFM.FM$LAYOUT,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public pkg.div_t div(
      int numerator,
      int denominator) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      return pkg.div_tFM.fromMemorySegment((java.lang.foreign.MemorySegment) FF$MH$2.invokeExact(
          (java.lang.foreign.SegmentAllocator) ff$arena,
          numerator,
          denominator));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$3 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("ldiv"),
      java.lang.foreign.FunctionDescriptor.of(
          pkg.ldiv_tFM.FM$LAYOUT,
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.JAVA_LONG));

  public pkg.@org.alveolo.ffm.Value ldiv_t ldiv(
      java.lang.foreign.SegmentAllocator allocator,
      long numerator,
      long denominator) {
    try {
      return new pkg.ldiv_tFM((java.lang.foreign.MemorySegment) FF$MH$3.invokeExact(
          allocator,
          numerator,
          denominator));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  // TODO support and use FF$LINKER.canonicalLayouts().get("size_t")
  // and how invokeExact would work with different size data?
  private static final java.lang.invoke.MethodHandle FF$MH$4 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("strlen"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS));

  public long strlen(
      java.lang.String utf8z) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      return (long) FF$MH$4.invokeExact(
          ff$arena.allocateFrom(utf8z));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$5 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("l64a"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_LONG));

  public java.lang.String l64a(
      long n) {
    try {
      var ff$string$r = (java.lang.foreign.MemorySegment) FF$MH$5.invokeExact(
          n);
      return ff$string$r.address() == 0L ? null
          : ff$string$r.reinterpret(Long.MAX_VALUE).getString(0L);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

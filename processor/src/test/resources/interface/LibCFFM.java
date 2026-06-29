package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class LibCFFM implements LibC {
  public static final LibCFFM INSTANCE = new LibCFFM();

  private LibCFFM() {}

  private static final Linker FF$LINKER = Linker.nativeLinker();

  private static final SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();

  private static final MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("abs"),
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

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

  private static final MethodHandle FF$MH$1 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("abs"),
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

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

  private static final MethodHandle FF$MH$2 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("div"),
      FunctionDescriptor.of(
          pkg.div_tFM.FM$LAYOUT,
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

  public pkg.div_t div(
      int numerator,
      int denominator) {
    try (var ff$arena = Arena.ofConfined()) {
      return pkg.div_tFM.fromMemorySegment((MemorySegment) FF$MH$2.invokeExact(
          (SegmentAllocator) ff$arena,
          numerator,
          denominator));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$3 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("ldiv"),
      FunctionDescriptor.of(
          pkg.ldiv_tFM.FM$LAYOUT,
          ValueLayout.JAVA_LONG,
          ValueLayout.JAVA_LONG));

  public pkg.@org.alveolo.ffm.Value ldiv_t ldiv(
      java.lang.foreign.SegmentAllocator allocator,
      long numerator,
      long denominator) {
    try {
      return new pkg.ldiv_tFM((MemorySegment) FF$MH$3.invokeExact(
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
  private static final MethodHandle FF$MH$4 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("strlen"),
      FunctionDescriptor.of(
          ValueLayout.JAVA_LONG,
          ValueLayout.ADDRESS));

  public long strlen(
      java.lang.String utf8z) {
    try (var ff$arena = Arena.ofConfined()) {
      return (long) FF$MH$4.invokeExact(
          ff$arena.allocateFrom(utf8z));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

//  private static final MethodHandle FF$MH$7 = FF$LINKER.downcallHandle(
//      FF$LOOKUP.findOrThrow("l64a"),
//      FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG));
//
//  public java.lang.String l64a(long n) {
//    try {
//      return ((MemorySegment) FF$MH$7.invokeExact(n))
//          .reinterpret(Long.MAX_VALUE).getString(0L);
//    } catch (RuntimeException|Error ff$e) {
//      throw ff$e;
//    } catch (Throwable ff$t) {
//      throw new AssertionError(ff$t);
//    }
//  }
}

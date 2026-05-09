package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class LibCFFM implements LibC {
  public static final LibCFFM INSTANCE = new LibCFFM();

  private LibCFFM() {}

  static {}

  private static final Linker FF$LINKER = Linker.nativeLinker();

  private static final SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();

  private static final MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("abs").get(),
      FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));

  public int abs(
      int number) {
    try {
      return (int) FF$MH$0.invokeExact(number);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$3 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("abs").get(),
      FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));

  public int renamed(int number) {
    try {
      return (int) FF$MH$3.invokeExact(number);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$4 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("div").get(),
      FunctionDescriptor.of(
          pkg.div_tFM.FM$LAYOUT,
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

  public pkg.div_t div(int numerator, int denominator) {
    try (var ff$arena = Arena.ofConfined()) {
      return pkg.div_tFM.fromMemorySegment((MemorySegment) FF$MH$4.invokeExact(
          (SegmentAllocator) ff$arena, numerator, denominator));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

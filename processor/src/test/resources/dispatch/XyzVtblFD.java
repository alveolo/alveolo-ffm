package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class XyzVtblFD implements XyzVtbl {
  private static final Linker FF$LINKER = Linker.nativeLinker();

  public static final MemoryLayout FD$LAYOUT =
      MemoryLayout.sequenceLayout(4L, ValueLayout.ADDRESS);

  public final MemorySegment ms;

  public XyzVtblFD(MemorySegment ms) {
    this.ms = ms.reinterpret(FD$LAYOUT.byteSize());
    this.FF$MH$0 = java.lang.invoke.MethodHandles.insertArguments(
        FF$MD$0, 0,
        this.ms.getAtIndex(ValueLayout.ADDRESS, 1L));
    this.FF$MH$1 = java.lang.invoke.MethodHandles.insertArguments(
        FF$MD$1, 0,
        this.ms.getAtIndex(ValueLayout.ADDRESS, 3L));
    this.FF$MH$2 = java.lang.invoke.MethodHandles.insertArguments(
        FF$MD$2, 0,
        this.ms.getAtIndex(ValueLayout.ADDRESS, 2L));
    this.FF$MH$3 = java.lang.invoke.MethodHandles.insertArguments(
        FF$MD$3, 0,
        this.ms.getAtIndex(ValueLayout.ADDRESS, 0L));
  }

  private static final MethodHandle FF$MD$0 = FF$LINKER.downcallHandle(
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

  private final MethodHandle FF$MH$0;

  public int add(
      int a,
      int b) {
    try {
      return (int) FF$MH$0.invokeExact(
          a,
          b);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MD$1 = FF$LINKER.downcallHandle(
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

  private final MethodHandle FF$MH$1;

  public int sub(
      int a,
      int b) {
    try {
      return (int) FF$MH$1.invokeExact(
          a,
          b);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MD$2 = FF$LINKER.downcallHandle(
      FunctionDescriptor.of(
          ValueLayout.JAVA_LONG,
          ValueLayout.ADDRESS));

  private final MethodHandle FF$MH$2;

  public long strlen(
      java.lang.String utf8z) {
    try (var ff$arena = Arena.ofConfined()) {
      return (long) FF$MH$2.invokeExact(
          ff$arena.allocateFrom(utf8z));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MD$3 = FF$LINKER.downcallHandle(
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.ADDRESS));

  private final MethodHandle FF$MH$3;

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var ff$arena = Arena.ofConfined()) {
      var ff$size$values = values.length;
      if (ff$size$values != 3) {
        throw new IllegalArgumentException("values length must be 3");
      }
      var ff$ms$values = ff$arena.allocate(ValueLayout.JAVA_INT, ff$size$values);
      MemorySegment.copy(values, 0, ff$ms$values, ValueLayout.JAVA_INT, 0, ff$size$values);
      return (int) FF$MH$3.invokeExact(
          ff$ms$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

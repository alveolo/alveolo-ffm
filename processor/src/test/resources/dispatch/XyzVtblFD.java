package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class XyzVtblFD implements XyzVtbl {
  private static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout FD$LAYOUT =
      java.lang.foreign.MemoryLayout.sequenceLayout(4L, java.lang.foreign.ValueLayout.ADDRESS);

  public static XyzVtblFD reinterpret(java.lang.foreign.MemorySegment ms) {
    return new XyzVtblFD(ms.reinterpret(FD$LAYOUT.byteSize()));
  }

  public final java.lang.foreign.MemorySegment ms;

  public XyzVtblFD(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
    this.FF$MH$0 = FF$MD$0.bindTo(
        ms.getAtIndex(java.lang.foreign.ValueLayout.ADDRESS, 1L));
    this.FF$MH$1 = FF$MD$1.bindTo(
        ms.getAtIndex(java.lang.foreign.ValueLayout.ADDRESS, 3L));
    this.FF$MH$2 = FF$MD$2.bindTo(
        ms.getAtIndex(java.lang.foreign.ValueLayout.ADDRESS, 2L));
    this.FF$MH$3 = FF$MD$3.bindTo(
        ms.getAtIndex(java.lang.foreign.ValueLayout.ADDRESS, 0L));
  }

  private static final java.lang.invoke.MethodHandle FF$MD$0 = FF$LINKER.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  private final java.lang.invoke.MethodHandle FF$MH$0;

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

  private static final java.lang.invoke.MethodHandle FF$MD$1 = FF$LINKER.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  private final java.lang.invoke.MethodHandle FF$MH$1;

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

  private static final java.lang.invoke.MethodHandle FF$MD$2 = FF$LINKER.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle FF$MH$2;

  public long strlen(
      java.lang.String utf8z) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      return (long) FF$MH$2.invokeExact(
          ff$arena.allocateFrom(utf8z));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MD$3 = FF$LINKER.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle FF$MH$3;

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$size$values = values.length;
      if (ff$size$values != 3) {
        throw new IllegalArgumentException("values length must be 3");
      }
      var ff$ms$values = ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT, ff$size$values);
      java.lang.foreign.MemorySegment.copy(values, 0, ff$ms$values, java.lang.foreign.ValueLayout.JAVA_INT, 0, ff$size$values);
      return (int) FF$MH$3.invokeExact(
          ff$ms$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

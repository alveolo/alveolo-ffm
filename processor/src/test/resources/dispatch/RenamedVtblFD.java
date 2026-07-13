package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class RenamedVtblFD implements RenamedVtbl {
  private static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout FD$LAYOUT =
      java.lang.foreign.MemoryLayout.sequenceLayout(1L, java.lang.foreign.ValueLayout.ADDRESS);

  public static RenamedVtblFD reinterpret(java.lang.foreign.MemorySegment ms) {
    return new RenamedVtblFD(ms.reinterpret(FD$LAYOUT.byteSize()));
  }

  public final java.lang.foreign.MemorySegment ms;

  public RenamedVtblFD(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
    this.FF$MH$0 = FF$MD$0.bindTo(
        ms.getAtIndex(java.lang.foreign.ValueLayout.ADDRESS, 0L));
  }

  private static final java.lang.invoke.MethodHandle FF$MD$0 = FF$LINKER.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  private final java.lang.invoke.MethodHandle FF$MH$0;

  public int call(
      int value) {
    try {
      return (int) FF$MH$0.invokeExact(
          value);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

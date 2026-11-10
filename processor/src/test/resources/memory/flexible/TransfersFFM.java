package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class TransfersFFM implements Transfers {
  public static final TransfersFFM INSTANCE$F = new TransfersFFM();

  private TransfersFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      SymbolLookup$F.find("submit")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public pkg.Names submit(
      pkg.Transfer transfer,
      pkg.Names source,
      pkg.NamesFM destination) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: submit");
    try {
      return pkg.NamesFM.reinterpret$F((java.lang.foreign.MemorySegment) MethodHandle$0$F.invokeExact(
          (java.lang.foreign.MemorySegment) (transfer == null ? java.lang.foreign.MemorySegment.NULL : ((pkg.TransferFM) transfer).MemorySegment$F),
          (java.lang.foreign.MemorySegment) (source == null ? java.lang.foreign.MemorySegment.NULL : ((pkg.NamesFM) source).MemorySegment$F),
          (java.lang.foreign.MemorySegment) (destination == null ? java.lang.foreign.MemorySegment.NULL : destination.MemorySegment$F)));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

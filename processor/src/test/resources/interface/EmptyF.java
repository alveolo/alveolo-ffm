package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
final class EmptyF implements Empty {
  public static final EmptyF INSTANCE$F = new EmptyF();

  private EmptyF() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();
}

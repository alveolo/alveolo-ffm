package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class EmptyFFM implements Empty {
  public static final EmptyFFM INSTANCE = new EmptyFFM();

  private EmptyFFM() {}

  public static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();
}

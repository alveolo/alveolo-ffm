package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class RenamedLibraryF implements LibraryApi {
  public static final RenamedLibraryF INSTANCE$F = new RenamedLibraryF();

  private RenamedLibraryF() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();
}

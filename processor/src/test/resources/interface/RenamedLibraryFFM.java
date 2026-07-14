package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class RenamedLibraryFFM implements LibraryApi {
  public static final RenamedLibraryFFM INSTANCE$F = new RenamedLibraryFFM();

  private RenamedLibraryFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();
}

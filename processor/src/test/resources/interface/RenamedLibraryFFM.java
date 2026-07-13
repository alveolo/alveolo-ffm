package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class RenamedLibraryFFM implements LibraryApi {
  public static final RenamedLibraryFFM INSTANCE = new RenamedLibraryFFM();

  private RenamedLibraryFFM() {}

  public static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();
}

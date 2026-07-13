package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class MultiLibraryFFM implements MultiLibrary {
  public static final MultiLibraryFFM INSTANCE = new MultiLibraryFFM();

  private MultiLibraryFFM() {}

  public static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup FF$LOOKUP = FF$LOOKUP();

  private static java.lang.foreign.SymbolLookup FF$LOOKUP() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        MultiLibrary.class,
        FF$LINKER.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "first", "",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.NAME),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "second", "",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.NAME));
  }
}

package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class MultiLibraryFFM implements MultiLibrary {
  public static final MultiLibraryFFM INSTANCE$F = new MultiLibraryFFM();

  private MultiLibraryFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = SymbolLookup$F();

  private static java.lang.foreign.SymbolLookup SymbolLookup$F() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        MultiLibrary.class,
        Linker$F.defaultLookup(),
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

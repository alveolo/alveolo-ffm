package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class MultiFrameworkF implements MultiFramework {
  public static final MultiFrameworkF INSTANCE$F = new MultiFrameworkF();

  private MultiFrameworkF() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = SymbolLookup$F();

  private static java.lang.foreign.SymbolLookup SymbolLookup$F() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        MultiFramework.class,
        Linker$F.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "Security", "",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.FRAMEWORK),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "Accelerate", "",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.FRAMEWORK));
  }
}

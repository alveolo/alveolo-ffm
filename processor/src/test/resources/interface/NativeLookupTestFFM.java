package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class NativeLookupTestFFM implements NativeLookupTest {
  public static final NativeLookupTestFFM INSTANCE$F = new NativeLookupTestFFM();

  private NativeLookupTestFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = SymbolLookup$F();

  private static java.lang.foreign.SymbolLookup SymbolLookup$F() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        NativeLookupTest.class,
        Linker$F.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "cups", "2",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.NAME));
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("add_ints"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public int add_ints(
      int left,
      int right) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          left,
          right);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

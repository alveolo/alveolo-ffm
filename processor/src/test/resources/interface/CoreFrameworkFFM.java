package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class CoreFrameworkFFM implements CoreFramework {
  public static final CoreFrameworkFFM INSTANCE$F = new CoreFrameworkFFM();

  private CoreFrameworkFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = SymbolLookup$F();

  private static java.lang.foreign.SymbolLookup SymbolLookup$F() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        CoreFramework.class,
        Linker$F.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "CoreFoundation", "A",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.FRAMEWORK));
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("CFAbsoluteTimeGetCurrent"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_DOUBLE));

  public double CFAbsoluteTimeGetCurrent(
      ) {
    try {
      return (double) MethodHandle$0$F.invokeExact(
          );
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

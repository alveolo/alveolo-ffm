package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class MultiFrameworkFFM implements MultiFramework {
  public static final MultiFrameworkFFM INSTANCE = new MultiFrameworkFFM();

  private MultiFrameworkFFM() {}

  public static final Linker FF$LINKER = Linker.nativeLinker();

  public static final SymbolLookup FF$LOOKUP = FF$LOOKUP();

  private static SymbolLookup FF$LOOKUP() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        MultiFramework.class,
        FF$LINKER.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "CoreFoundation", "",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.FRAMEWORK),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "IOKit", "",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.FRAMEWORK));
  }
}

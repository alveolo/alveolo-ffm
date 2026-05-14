package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class MultiFrameworkFFM implements MultiFramework {
  public static final MultiFrameworkFFM INSTANCE = new MultiFrameworkFFM();

  private MultiFrameworkFFM() {}

  static {
    System.load("/System/Library/Frameworks/CoreFoundation.framework"
        + "/Versions/Current/CoreFoundation");
    System.load("/System/Library/Frameworks/IOKit.framework"
        + "/Versions/Current/IOKit");
  }

  private static final Linker FF$LINKER = Linker.nativeLinker();

  private static final SymbolLookup FF$LOOKUP = SymbolLookup.loaderLookup();
}

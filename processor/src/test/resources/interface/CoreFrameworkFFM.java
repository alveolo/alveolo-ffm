package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class CoreFrameworkFFM implements CoreFramework {
  public static final CoreFrameworkFFM INSTANCE = new CoreFrameworkFFM();

  private CoreFrameworkFFM() {}

  static {
    System.load("/System/Library/Frameworks/CoreFoundation.framework"
        + "/Versions/Current/CoreFoundation");
  }

  private static final Linker FF$LINKER = Linker.nativeLinker();

  private static final SymbolLookup FF$LOOKUP = SymbolLookup.loaderLookup();

  private static final MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("CFAbsoluteTimeGetCurrent").get(),
      FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE));

  public double CFAbsoluteTimeGetCurrent() {
    try {
      return (double) FF$MH$0.invokeExact();
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

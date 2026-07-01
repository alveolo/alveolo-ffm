package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class CoreFrameworkFFM implements CoreFramework {
  public static final CoreFrameworkFFM INSTANCE = new CoreFrameworkFFM();

  private CoreFrameworkFFM() {}

  public static final Linker FF$LINKER = Linker.nativeLinker();

  public static final SymbolLookup FF$LOOKUP = FF$LOOKUP();

  private static SymbolLookup FF$LOOKUP() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        CoreFramework.class,
        FF$LINKER.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "CoreFoundation", "A",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.FRAMEWORK));
  }

  private static final MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("CFAbsoluteTimeGetCurrent"),
      FunctionDescriptor.of(
          ValueLayout.JAVA_DOUBLE));

  public double CFAbsoluteTimeGetCurrent(
      ) {
      try {
        return (double) FF$MH$0.invokeExact(
          );
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class NativeLookupTestFFM implements NativeLookupTest {
  public static final NativeLookupTestFFM INSTANCE = new NativeLookupTestFFM();

  private NativeLookupTestFFM() {}

  private static final Linker FF$LINKER = Linker.nativeLinker();

  private static final SymbolLookup FF$LOOKUP = FF$LOOKUP();

  private static SymbolLookup FF$LOOKUP() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        NativeLookupTest.class,
        FF$LINKER.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "cups", "2",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.NAME)
    );
  }

  private static final MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("add_ints").get(),
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

  public int add_ints(int left, int right) {
    try {
        return (int) FF$MH$0.invokeExact(left, right);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class NativeTestFFM implements NativeTest {
  public static final NativeTestFFM INSTANCE = new NativeTestFFM();

  private NativeTestFFM() {}

  static {
    System.loadLibrary("alveolo_native_test");
  }

  private static final Linker FF$LINKER = Linker.nativeLinker();

  private static final SymbolLookup FF$LOOKUP = SymbolLookup.loaderLookup();

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

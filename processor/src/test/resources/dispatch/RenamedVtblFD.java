package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class RenamedVtblFD implements RenamedVtbl {
  private static final Linker FF$LINKER = Linker.nativeLinker();

  public static final MemoryLayout FD$LAYOUT =
      MemoryLayout.sequenceLayout(1L, ValueLayout.ADDRESS);

  public static RenamedVtblFD reinterpret(MemorySegment ms) {
    return new RenamedVtblFD(ms.reinterpret(FD$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public RenamedVtblFD(MemorySegment ms) {
    this.ms = ms;
    this.FF$MH$0 = FF$MD$0.bindTo(
        ms.getAtIndex(ValueLayout.ADDRESS, 0L));
  }

  private static final MethodHandle FF$MD$0 = FF$LINKER.downcallHandle(
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

  private final MethodHandle FF$MH$0;

  public int call(
      int value) {
    try {
      return (int) FF$MH$0.invokeExact(
          value);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

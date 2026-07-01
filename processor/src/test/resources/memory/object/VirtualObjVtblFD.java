package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class VirtualObjVtblFD implements VirtualObjVtbl {
  private static final Linker FF$LINKER = Linker.nativeLinker();

  public static final MemoryLayout FD$LAYOUT =
      MemoryLayout.sequenceLayout(5L, ValueLayout.ADDRESS);

  public static VirtualObjVtblFD reinterpret(MemorySegment ms) {
    return new VirtualObjVtblFD(ms.reinterpret(FD$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public VirtualObjVtblFD(MemorySegment ms) {
    this.ms = ms;
    this.FF$MH$0 = FF$MD$0.bindTo(
        ms.getAtIndex(ValueLayout.ADDRESS, 2L));
    this.FF$MH$1 = FF$MD$1.bindTo(
        ms.getAtIndex(ValueLayout.ADDRESS, 4L));
  }

  private static final MethodHandle FF$MD$0 = FF$LINKER.downcallHandle(
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.ADDRESS,
          ValueLayout.JAVA_INT));

  private final MethodHandle FF$MH$0;

  public int method(
      pkg.VirtualObj ff$self,
      int arg) {
    try {
      return (int) FF$MH$0.invokeExact(
          ((pkg.VirtualObjFM)ff$self).ms,
          arg);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MD$1 = FF$LINKER.downcallHandle(
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.ADDRESS,
          ValueLayout.ADDRESS));

  private final MethodHandle FF$MH$1;

  public int sum(
      pkg.VirtualObj ff$self,
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var ff$arena = Arena.ofConfined()) {
      var ff$size$values = values.length;
      if (ff$size$values != 3) {
        throw new IllegalArgumentException("values length must be 3");
      }
      var ff$ms$values = ff$arena.allocate(ValueLayout.JAVA_INT, ff$size$values);
      MemorySegment.copy(values, 0, ff$ms$values, ValueLayout.JAVA_INT, 0, ff$size$values);
      return (int) FF$MH$1.invokeExact(
          ((pkg.VirtualObjFM)ff$self).ms,
          ff$ms$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

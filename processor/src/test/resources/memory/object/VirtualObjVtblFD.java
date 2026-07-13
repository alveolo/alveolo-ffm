package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class VirtualObjVtblFD implements VirtualObjVtbl {
  private static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout FD$LAYOUT =
      java.lang.foreign.MemoryLayout.sequenceLayout(5L, java.lang.foreign.ValueLayout.ADDRESS);

  public static VirtualObjVtblFD reinterpret(java.lang.foreign.MemorySegment ms) {
    return new VirtualObjVtblFD(ms.reinterpret(FD$LAYOUT.byteSize()));
  }

  public final java.lang.foreign.MemorySegment ms;

  public VirtualObjVtblFD(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
    this.FF$MH$0 = FF$MD$0.bindTo(
        ms.getAtIndex(java.lang.foreign.ValueLayout.ADDRESS, 2L));
    this.FF$MH$1 = FF$MD$1.bindTo(
        ms.getAtIndex(java.lang.foreign.ValueLayout.ADDRESS, 4L));
  }

  private static final java.lang.invoke.MethodHandle FF$MD$0 = FF$LINKER.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  private final java.lang.invoke.MethodHandle FF$MH$0;

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

  private static final java.lang.invoke.MethodHandle FF$MD$1 = FF$LINKER.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle FF$MH$1;

  public int sum(
      pkg.VirtualObj ff$self,
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$size$values = values.length;
      if (ff$size$values != 3) {
        throw new IllegalArgumentException("values length must be 3");
      }
      var ff$ms$values = ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT, ff$size$values);
      java.lang.foreign.MemorySegment.copy(values, 0, ff$ms$values, java.lang.foreign.ValueLayout.JAVA_INT, 0, ff$size$values);
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

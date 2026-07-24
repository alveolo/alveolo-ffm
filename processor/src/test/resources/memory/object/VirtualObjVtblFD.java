package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class VirtualObjVtblFD implements VirtualObjVtbl {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.sequenceLayout(5L,
          java.lang.foreign.ValueLayout.ADDRESS);

  public static VirtualObjVtblFD reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new VirtualObjVtblFD(memorySegment.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public VirtualObjVtblFD(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
    this.MethodHandle$0$F = DowncallHandle$0$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 2L));
    this.MethodHandle$1$F = DowncallHandle$1$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 4L));
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$0$F =
      org.alveolo.ffm.NativeTypes.adaptDowncall(
          Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              java.lang.foreign.ValueLayout.ADDRESS,
              org.alveolo.ffm.NativeTypes.C_LONG_LAYOUT),
              java.lang.foreign.Linker.Option.firstVariadicArg(2)),
          null,
          new org.alveolo.ffm.NativeTypes.Type[] {
              null,
              null,
              org.alveolo.ffm.NativeTypes.Type.SLONG
          });

  private final java.lang.invoke.MethodHandle MethodHandle$0$F;

  public int method(
      pkg.VirtualObj self$f,
      long arg) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          ((pkg.VirtualObjFM) self$f).MemorySegment$F,
          arg);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$1$F =
      Linker$F.downcallHandle(
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$1$F;

  public int sum(
      pkg.VirtualObj self$f,
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      if (values$size$f != 3) {
        throw new IllegalArgumentException(
            "values length must be 3");
      }
      var values$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      java.lang.foreign.MemorySegment.copy(
          values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      return (int) MethodHandle$1$F.invokeExact(
          ((pkg.VirtualObjFM) self$f).MemorySegment$F,
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

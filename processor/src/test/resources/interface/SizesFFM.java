package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class SizesFFM implements Sizes {
  public static final SizesFFM INSTANCE$F = new SizesFFM();

  private SizesFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      org.alveolo.ffm.NativeType.adaptDowncall(
          Linker$F.downcallHandle(
              SymbolLookup$F.findOrThrow("echo"),
              java.lang.foreign.FunctionDescriptor.of(
                  org.alveolo.ffm.NativeType.SIZE_T.layout,
                  org.alveolo.ffm.NativeType.SIZE_T.layout)),
          org.alveolo.ffm.NativeType.SIZE_T,
          new org.alveolo.ffm.NativeType[] {
              org.alveolo.ffm.NativeType.SIZE_T
          });

  public long echo(
      long value) {
    try {
      return (long) MethodHandle$0$F.invokeExact(
          value);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      org.alveolo.ffm.NativeType.adaptDowncall(
          Linker$F.downcallHandle(
              SymbolLookup$F.findOrThrow("read"),
              java.lang.foreign.FunctionDescriptor.of(
                  org.alveolo.ffm.NativeType.SIZE_T.layout,
                  java.lang.foreign.ValueLayout.ADDRESS)),
          org.alveolo.ffm.NativeType.SIZE_T,
          new org.alveolo.ffm.NativeType[] {
              null
          });

  public long read(
      long value) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var value$MemorySegment$f = arena$f.allocate(org.alveolo.ffm.NativeType.SIZE_T.layout);
      org.alveolo.ffm.NativeType.setSizeT(value$MemorySegment$f, 0L, value);
      return (long) MethodHandle$1$F.invokeExact(
          value$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      org.alveolo.ffm.NativeType.adaptDowncall(
          Linker$F.downcallHandle(
              SymbolLookup$F.findOrThrow("sum"),
              java.lang.foreign.FunctionDescriptor.of(
                  org.alveolo.ffm.NativeType.SIZE_T.layout,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS)),
          org.alveolo.ffm.NativeType.SIZE_T,
          new org.alveolo.ffm.NativeType[] {
              null,
              null
          });

  public long sum(
      long first,
      long second) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var first$allocationOffset$f = 0L;
      var allocationOffset$f = org.alveolo.ffm.NativeType.SIZE_T.layout.byteSize();
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, org.alveolo.ffm.NativeType.SIZE_T.layout.byteAlignment()));
      var second$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, org.alveolo.ffm.NativeType.SIZE_T.layout.byteSize());
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, org.alveolo.ffm.NativeType.SIZE_T.layout.byteAlignment());
      var first$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          first$allocationOffset$f, org.alveolo.ffm.NativeType.SIZE_T.layout.byteSize());
      org.alveolo.ffm.NativeType.setSizeT(first$MemorySegment$f, 0L, first);
      var second$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          second$allocationOffset$f, org.alveolo.ffm.NativeType.SIZE_T.layout.byteSize());
      org.alveolo.ffm.NativeType.setSizeT(second$MemorySegment$f, 0L, second);
      return (long) MethodHandle$2$F.invokeExact(
          first$MemorySegment$f,
          second$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$3$F =
      Linker$F.downcallHandle(
          SymbolLookup$F.findOrThrow("address"),
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.ADDRESS));

  public long address(
      ) {
    try {
      var addressResult$f = (java.lang.foreign.MemorySegment) MethodHandle$3$F.invokeExact(
          );
      org.alveolo.ffm.ForeignUtils.requireNonNullAddress(addressResult$f);
      return org.alveolo.ffm.NativeType.getSizeT(addressResult$f.reinterpret(org.alveolo.ffm.NativeType.SIZE_T.layout.byteSize()), 0L);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

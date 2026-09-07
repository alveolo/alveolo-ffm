package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class NullableCallsFFM implements NullableCalls {
  public static final NullableCallsFFM INSTANCE$F = new NullableCallsFFM();

  private NullableCallsFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      Linker$F.downcallHandle(
          SymbolLookup$F.findOrThrow("find"),
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.ADDRESS));

  public pkg.@org.alveolo.ffm.Address NullItem find(
      ) {
    try {
      return pkg.NullItemFM.reinterpret$F((java.lang.foreign.MemorySegment) MethodHandle$0$F.invokeExact(
          ));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      Linker$F.downcallHandle(
          SymbolLookup$F.findOrThrow("number"),
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.ADDRESS));

  public int number(
      ) {
    try {
      var addressResult$f = (java.lang.foreign.MemorySegment) MethodHandle$1$F.invokeExact(
          );
      org.alveolo.ffm.ForeignUtils.requireNonNullAddress(addressResult$f);
      return addressResult$f.reinterpret(java.lang.foreign.ValueLayout.JAVA_INT.byteSize())
          .get(java.lang.foreign.ValueLayout.JAVA_INT, 0L);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      Linker$F.downcallHandle(
          SymbolLookup$F.findOrThrow("accept"),
          java.lang.foreign.FunctionDescriptor.ofVoid(
              java.lang.foreign.ValueLayout.ADDRESS,
              java.lang.foreign.ValueLayout.ADDRESS));

  public void accept(
      pkg.NullItem item,
      java.lang.String text) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var text$bytes$f = text == null ? null : text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
      var item$allocationOffset$f = 0L;
      var allocationOffset$f = (item == null ? 0L : pkg.NullItemFM.MemoryLayout$F.byteSize());
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, 1L));
      var text$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, (text == null ? 0L : Math.addExact((long) text$bytes$f.length, 1L)));
      var allocation$MemorySegment$f = arena$f.allocate(
          allocationOffset$f, pkg.NullItemFM.MemoryLayout$F.byteAlignment());
      var item$MemorySegment$f = (java.lang.foreign.MemorySegment) (item == null ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          item$allocationOffset$f, (item == null ? 0L : pkg.NullItemFM.MemoryLayout$F.byteSize())));
      if (item != null) {
        pkg.NullItemFM.toMemorySegment$F(item, item$MemorySegment$f);
      }
      var text$MemorySegment$f = text == null
          ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          text$allocationOffset$f, (text == null ? 0L : Math.addExact((long) text$bytes$f.length, 1L)));
      if (text != null) {
        java.lang.foreign.MemorySegment.copy(
            text$bytes$f, 0, text$MemorySegment$f,
            java.lang.foreign.ValueLayout.JAVA_BYTE, 0, text$bytes$f.length);
        text$MemorySegment$f.set(
            java.lang.foreign.ValueLayout.JAVA_BYTE, text$bytes$f.length,
            (byte) 0);
      }
      MethodHandle$2$F.invokeExact(
          item$MemorySegment$f,
          text$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

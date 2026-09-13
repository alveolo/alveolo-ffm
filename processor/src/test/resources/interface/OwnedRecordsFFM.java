package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class OwnedRecordsFFM implements OwnedRecords {
  public static final OwnedRecordsFFM INSTANCE$F = new OwnedRecordsFFM();

  private OwnedRecordsFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      SymbolLookup$F.find("create")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public pkg.@org.alveolo.ffm.Address @org.alveolo.ffm.Release("free") OwnedItem create(
      @org.alveolo.ffm.Out int[] values) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: create");
    if (MethodHandle$1$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: free");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values == null ? 0 : values.length;
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
      var nativeResult$f = (java.lang.foreign.MemorySegment) MethodHandle$0$F.invokeExact(
          values$MemorySegment$f);
      try (var release$f = (java.lang.AutoCloseable) () -> {
        if (!nativeResult$f.equals(java.lang.foreign.MemorySegment.NULL))
          free(nativeResult$f);
      }) {
        if (values$size$f != 0) {
          java.lang.foreign.MemorySegment.copy(
              values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, values$size$f);
        }
        return pkg.OwnedItemFM.reinterpret$F((java.lang.foreign.MemorySegment) nativeResult$f);
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      SymbolLookup$F.find("free")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void free(
      java.lang.foreign.MemorySegment pointer) {
    if (MethodHandle$1$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: free");
    try {
      MethodHandle$1$F.invokeExact(
          pointer);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

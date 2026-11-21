package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
final class ZeroCallsF implements ZeroCalls {
  public static final ZeroCallsF INSTANCE$F = new ZeroCallsF();

  private ZeroCallsF() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      SymbolLookup$F.find("snapshots")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void snapshots(
      pkg.EmptyRecord first,
      pkg.EmptyRecord second) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: snapshots");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var first$allocationOffset$f = 0L;
      var allocationOffset$f = (first == null ? 0L : Math.max(1L, pkg.EmptyRecordF.MemoryLayout$F.byteSize()));
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, pkg.EmptyRecordF.MemoryLayout$F.byteAlignment()));
      var second$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, (second == null ? 0L : Math.max(1L, pkg.EmptyRecordF.MemoryLayout$F.byteSize())));
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, pkg.EmptyRecordF.MemoryLayout$F.byteAlignment());
      var first$MemorySegment$f = (java.lang.foreign.MemorySegment) (first == null ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          first$allocationOffset$f, (first == null ? 0L : Math.max(1L, pkg.EmptyRecordF.MemoryLayout$F.byteSize()))));
      if (first != null) {
        pkg.EmptyRecordF.toMemorySegment$F(first, first$MemorySegment$f);
      }
      var second$MemorySegment$f = (java.lang.foreign.MemorySegment) (second == null ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          second$allocationOffset$f, (second == null ? 0L : Math.max(1L, pkg.EmptyRecordF.MemoryLayout$F.byteSize()))));
      if (second != null) {
        pkg.EmptyRecordF.toMemorySegment$F(second, second$MemorySegment$f);
      }
      MethodHandle$0$F.invokeExact(
          first$MemorySegment$f,
          second$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

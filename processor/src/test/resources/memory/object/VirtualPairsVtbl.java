package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
@org.alveolo.ffm.DispatchTable
interface VirtualPairsVtbl {
  @org.alveolo.ffm.Slot(0)
  pkg.@org.alveolo.ffm.Value Pair make(
      java.lang.foreign.SegmentAllocator allocator,
      VirtualPairs self$f,
      int left,
      int right);

  @org.alveolo.ffm.FirstVariadicArg(2)
  @org.alveolo.ffm.Slot(1)
  pkg.@org.alveolo.ffm.Value Pair variadic(
      java.lang.foreign.SegmentAllocator allocator,
      VirtualPairs self$f,
      pkg.ErrorState capture,
      int count,
      int value);
}

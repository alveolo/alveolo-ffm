package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
@org.alveolo.ffm.DispatchTable
interface CountedVirtualVtbl {
  @org.alveolo.ffm.Slot(0)
  void fill(
      CountedVirtual self$f,
      @org.alveolo.ffm.CountedBy("count")
      @org.alveolo.ffm.Out int[] values,
      int count);

  @org.alveolo.ffm.Slot(1)
  int count(
      CountedVirtual self$f,
      @org.alveolo.ffm.CountedBy("length") int[] values,
      long length);
}

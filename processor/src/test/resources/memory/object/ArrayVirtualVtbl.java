package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
@org.alveolo.ffm.DispatchTable
interface ArrayVirtualVtbl {
  @org.alveolo.ffm.Slot(0)
  void fill(
      ArrayVirtual self$f,
      @org.alveolo.ffm.Out int[] values,
      int count);

  @org.alveolo.ffm.Slot(1)
  int count(
      ArrayVirtual self$f,
      int[] values,
      long length);
}

package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
@org.alveolo.ffm.DispatchTable
interface VirtualObjVtbl {

  @org.alveolo.ffm.FirstVariadicArg(2)
  @org.alveolo.ffm.Slot(2)
  int method(
      VirtualObj self$f,
      @org.alveolo.ffm.CLong long arg);

  @org.alveolo.ffm.Slot(4)
  int sum(
      VirtualObj self$f,
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values);
}

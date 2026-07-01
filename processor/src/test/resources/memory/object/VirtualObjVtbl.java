package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
@org.alveolo.ffm.DispatchTable
interface VirtualObjVtbl {

  @org.alveolo.ffm.Slot(2)
  int method(
      VirtualObj ff$self,
      int arg);

  @org.alveolo.ffm.Slot(4)
  int sum(
      VirtualObj ff$self,
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values);
}

package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
@org.alveolo.ffm.DispatchTable
interface VirtualStructVtblSpec {

  @org.alveolo.ffm.Slot(0)
  int generatedCircular(
      VirtualStructSpec self$f,
      passmode.CircularDefault defaultValue,
      passmode.@org.alveolo.ffm.Value CircularValue value,
      passmode.@org.alveolo.ffm.Address CircularAddress address);

  @org.alveolo.ffm.Slot(1)
  int generatedOverrides(
      VirtualStructSpec self$f,
      passmode.@org.alveolo.ffm.Value CircularAddress value,
      passmode.@org.alveolo.ffm.Address CircularValue address);
}

module org.alveolo.ffm.processor {
  requires java.compiler;
  requires java.logging;
  requires org.alveolo.ffm;

  provides javax.annotation.processing.Processor with
      org.alveolo.ffm.processor.CallStateProcessor,
      org.alveolo.ffm.processor.DispatchTableProcessor,
      org.alveolo.ffm.processor.ForeignInterfaceProcessor,
      org.alveolo.ffm.processor.ForeignMemoryProcessor;
}

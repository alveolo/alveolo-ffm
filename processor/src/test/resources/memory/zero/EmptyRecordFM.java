package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class EmptyRecordFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
      }));

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static EmptyRecord reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : fromMemorySegment$F(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static void toMemorySegment$F(
      EmptyRecord source,
      java.lang.foreign.MemorySegment memorySegment) {
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      EmptyRecord source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment);
    return memorySegment;
  }

  public static EmptyRecord fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new EmptyRecord();
  }
}

package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
final class EmptyUnionF implements EmptyUnion {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.unionLayout(
          org.alveolo.ffm.ForeignUtils.unionPad(
              new java.lang.foreign.MemoryLayout [] {
          }));

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static EmptyUnionF reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : new EmptyUnionF(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public EmptyUnionF(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public EmptyUnionF(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }
}

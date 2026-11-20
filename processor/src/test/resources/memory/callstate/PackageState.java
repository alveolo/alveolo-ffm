package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.CallStateProcessor")
final class PackageState implements PackageStateSpec {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.Linker.Option.captureStateLayout();

  public static final String StateName$F =
      org.alveolo.ffm.ForeignUtils.callStateName(
          "errno");

  public static final java.lang.foreign.Linker.Option LinkerOption$F =
      java.lang.foreign.Linker.Option.captureCallState(StateName$F);

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
        MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static PackageState reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new PackageState(memorySegment.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public PackageState(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public PackageState(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      error$PathElement$F = java.lang.foreign.MemoryLayout
          .PathElement.groupElement(StateName$F);

  public static final java.lang.invoke.VarHandle error$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(error$PathElement$F), 1, 0L);

  public int error() {
    return (int) error$VarHandle$F.get(MemorySegment$F);
  }
}

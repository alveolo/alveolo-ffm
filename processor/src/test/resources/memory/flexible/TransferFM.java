package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class TransferFM implements Transfer {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("packetsOrStreamId"),
        java.lang.foreign.MemoryLayout.sequenceLayout(0L,
            pkg.PacketFM.MemoryLayout$F).withName("packets"),
      }));

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator, long flexibleLength) {
    return allocator.allocate(
        Math.max(MemoryLayout$F.byteSize(), packets$RowLayout$F.scale(packets$Offset$F, flexibleLength)),
        MemoryLayout$F.byteAlignment());
  }

  public static TransferFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment, long flexibleLength) {
    long byteSize = Math.max(MemoryLayout$F.byteSize(), packets$RowLayout$F.scale(packets$Offset$F, flexibleLength));
    if (memorySegment.equals(java.lang.foreign.MemorySegment.NULL)) return null;
    return new TransferFM(memorySegment.reinterpret(byteSize), flexibleLength);
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public final java.lang.foreign.MemorySegment packets$MemorySegment$F;

  public TransferFM(java.lang.foreign.SegmentAllocator allocator, long flexibleLength) {
    this(allocate$F(allocator, flexibleLength), flexibleLength);
  }

  public TransferFM(java.lang.foreign.MemorySegment memorySegment, long flexibleLength) {
    this.MemorySegment$F = memorySegment;
    this.packets$MemorySegment$F = memorySegment.asSlice(
        packets$Offset$F, packets$RowLayout$F.scale(0L, flexibleLength));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      packetsOrStreamId$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("packetsOrStreamId");

  public static final java.lang.invoke.VarHandle packetsOrStreamId$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(packetsOrStreamId$PathElement$F), 1, 0L);

  public int packetsOrStreamId() {
    return (int) packetsOrStreamId$VarHandle$F.get(MemorySegment$F);
  }

  public TransferFM packetsOrStreamId(int value) {
    packetsOrStreamId$VarHandle$F.set(MemorySegment$F, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      packets$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("packets");

  public static final java.lang.foreign.MemoryLayout
      packets$MemoryLayout$F =
          MemoryLayout$F.select(packets$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      packets$ElementMemoryLayout$F =
      pkg.PacketFM.MemoryLayout$F;

  private static final java.lang.foreign.MemoryLayout packets$RowLayout$F =
      ((java.lang.foreign.SequenceLayout) packets$MemoryLayout$F).elementLayout();
  private static final long packets$Offset$F =
      MemoryLayout$F.byteOffset(packets$PathElement$F);

  public java.lang.foreign.MemorySegment
      packetsAsMemorySegment$F(long index0) {
    return packets$MemorySegment$F.asSlice(
        Math.multiplyExact(index0, packets$RowLayout$F.byteSize()),
        packets$ElementMemoryLayout$F.byteSize());
  }

  public pkg.PacketFM packets(long index0$f) {
    return new pkg.PacketFM(
        packetsAsMemorySegment$F(index0$f));
  }

  public TransferFM packets(
      long index0$f,
      pkg.PacketFM value$f) {
    java.lang.foreign.MemorySegment.copy(
        value$f.MemorySegment$F, 0L,
        packetsAsMemorySegment$F(index0$f), 0L, packets$ElementMemoryLayout$F.byteSize());
    return this;
  }
}

package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class NamesFM implements Names {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("count"),
        java.lang.foreign.MemoryLayout.sequenceLayout(0L,
            java.lang.foreign.MemoryLayout.sequenceLayout(10L,
                java.lang.foreign.ValueLayout.JAVA_BYTE)).withName("rows"),
      }));

  public static java.lang.foreign.MemorySegment allocateFlexible$F(
      java.lang.foreign.SegmentAllocator allocator,
      int flexibleLength) {
    var memorySegment = allocator.allocate(
        Math.max(MemoryLayout$F.byteSize(), rows$RowLayout$F.scale(rows$Offset$F, flexibleLength)),
        MemoryLayout$F.byteAlignment());
    count$VarHandle$F.set(memorySegment, flexibleLength);
    return memorySegment;
  }

  public static NamesFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    if (memorySegment.equals(java.lang.foreign.MemorySegment.NULL)) return null;
    int flexibleLength = readFlexibleLength$F(
        memorySegment.reinterpret(MemoryLayout$F.byteSize()));
    return reinterpretFlexible$F(memorySegment, flexibleLength);
  }

  public static NamesFM reinterpretFlexible$F(
      java.lang.foreign.MemorySegment memorySegment,
      int flexibleLength) {
    long byteSize = Math.max(MemoryLayout$F.byteSize(), rows$RowLayout$F.scale(rows$Offset$F, flexibleLength));
    if (memorySegment.equals(java.lang.foreign.MemorySegment.NULL)) return null;
    return new NamesFM(memorySegment.reinterpret(byteSize), flexibleLength);
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public final java.lang.foreign.MemorySegment rows$MemorySegment$F;

  public NamesFM(java.lang.foreign.SegmentAllocator allocator, int flexibleLength) {
    this(allocateFlexible$F(allocator, flexibleLength), flexibleLength);
  }

  public NamesFM(java.lang.foreign.MemorySegment memorySegment) {
    this(memorySegment, readFlexibleLength$F(memorySegment));
  }

  public NamesFM(java.lang.foreign.MemorySegment memorySegment, int flexibleLength) {
    this(memorySegment, memorySegment.asSlice(
        rows$Offset$F, rows$RowLayout$F.scale(0L, flexibleLength)));
  }

  private NamesFM(java.lang.foreign.MemorySegment memorySegment,
      java.lang.foreign.MemorySegment flexibleMemorySegment) {
    this.MemorySegment$F = memorySegment;
    this.rows$MemorySegment$F = flexibleMemorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      count$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("count");

  public static final java.lang.invoke.VarHandle count$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(count$PathElement$F), 1, 0L);

  private static int readFlexibleLength$F(java.lang.foreign.MemorySegment memorySegment) {
    return (int) count$VarHandle$F.get(memorySegment);
  }

  public int count() {
    return (int) count$VarHandle$F.get(MemorySegment$F);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      rows$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("rows");

  public static final java.lang.foreign.MemoryLayout.PathElement
      rows$Sequence1PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      rows$MemoryLayout$F =
          MemoryLayout$F.select(rows$PathElement$F);

  private static final java.lang.foreign.MemoryLayout rows$RowLayout$F =
      ((java.lang.foreign.SequenceLayout) rows$MemoryLayout$F).elementLayout();
  private static final long rows$Offset$F =
      MemoryLayout$F.byteOffset(rows$PathElement$F);

  public static final long rows$Sequence1Dimension$F =
      10L;

  public static final java.lang.invoke.VarHandle rows$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          rows$RowLayout$F.arrayElementVarHandle(rows$Sequence1PathElement$F), 1, 0L);

  public byte rows(long index0$f, int index1$f) {
    return (byte) rows$VarHandle$F.get(rows$MemorySegment$F, index0$f, index1$f);
  }

  public NamesFM rows(
      long index0$f, int index1$f,
      byte value$f) {
    rows$VarHandle$F.set(rows$MemorySegment$F, index0$f, index1$f, value$f);
    return this;
  }
}

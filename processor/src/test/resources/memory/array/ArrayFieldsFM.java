package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ArrayFieldsFM implements ArrayFields {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.MemoryLayout.sequenceLayout(3L,
            java.lang.foreign.ValueLayout.JAVA_BOOLEAN).withName("flags"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            java.lang.foreign.MemoryLayout.sequenceLayout(3L,
                java.lang.foreign.ValueLayout.JAVA_INT)).withName("matrix"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            pkg.ArrayPointFM.MemoryLayout$F).withName("points"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            java.lang.foreign.ValueLayout.ADDRESS).withName("pointers"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            pkg.ArrayCellFM.MemoryLayout$F).withName("cells"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            java.lang.foreign.ValueLayout.ADDRESS).withName("references"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            java.lang.foreign.ValueLayout.ADDRESS).withName("raw"),
      }));

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator$f) {
    return allocator$f.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator$f, long count$f) {
    if (count$f < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator$f.allocate(MemoryLayout$F, count$f);
  }

  public static ArrayFieldsFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new ArrayFieldsFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f, long count$f) {
    if (count$f < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return memorySegment$f.reinterpret(Math.multiplyExact(
        MemoryLayout$F.byteSize(), count$f));
  }

  private static java.lang.foreign.MemorySegment elementAt$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    if (index$f < 0) {
      throw new IndexOutOfBoundsException(index$f);
    }
    return array$f.asSlice(Math.multiplyExact(
        index$f, MemoryLayout$F.byteSize()), MemoryLayout$F.byteSize());
  }

  public static ArrayFieldsFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new ArrayFieldsFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public ArrayFieldsFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public ArrayFieldsFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      flags$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("flags");

  public static final java.lang.foreign.MemoryLayout.PathElement
      flags$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      flags$MemoryLayout$F =
          MemoryLayout$F.select(flags$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      flags$ElementMemoryLayout$F =
      java.lang.foreign.ValueLayout.JAVA_BOOLEAN;

  public static final long flags$Sequence0Dimension$F = 3L;

  public static final java.lang.invoke.VarHandle flags$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(
              flags$PathElement$F, flags$Sequence0PathElement$F), 1, 0L);

  public java.lang.foreign.MemorySegment
      flagsAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(flags$PathElement$F),
        flags$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      flagsAsMemorySegment$F(int index$f) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            flags$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        flags$ElementMemoryLayout$F.byteSize());
  }

  public boolean flags(int index) {
    return (boolean) flags$VarHandle$F.get(MemorySegment$F, index);
  }

  public ArrayFieldsFM flags(
      int index,
      boolean value$f) {
    flags$VarHandle$F.set(MemorySegment$F, index, value$f);
    return this;
  }

  public java.nio.ByteBuffer flagsAsBuffer$F() {
    return flagsAsMemorySegment$F().asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder());
  }

  public boolean[] flagsToArray$F() {
    var result$f =
        new boolean[(int) flags$Sequence0Dimension$F];
    for (int index$f = 0;
        index$f < result$f.length; index$f++) {
      result$f[(int) index$f] = flags(index$f);
    }
    return result$f;
  }

  public ArrayFieldsFM flagsFromArray$F(boolean[] value$f) {
    java.util.Objects.requireNonNull(value$f, "value");
    if (value$f.length != flags$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "flags length must be " + flags$Sequence0Dimension$F);
    }
    for (int index$f = 0;
        index$f < value$f.length; index$f++) {
      flags(index$f, value$f[(int) index$f]);
    }
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      matrix$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("matrix");

  public static final java.lang.foreign.MemoryLayout.PathElement
      matrix$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout.PathElement
      matrix$Sequence1PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      matrix$MemoryLayout$F =
          MemoryLayout$F.select(matrix$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      matrix$ElementMemoryLayout$F =
      java.lang.foreign.ValueLayout.JAVA_INT;

  public static final long matrix$Sequence0Dimension$F = 2L;

  public static final long matrix$Sequence1Dimension$F = 3L;

  public static final java.lang.invoke.VarHandle matrix$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(
              matrix$PathElement$F, matrix$Sequence0PathElement$F, matrix$Sequence1PathElement$F), 1, 0L);

  public java.lang.foreign.MemorySegment
      matrixAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(matrix$PathElement$F),
        matrix$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      matrixAsMemorySegment$F(long row$f, long column$f) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            matrix$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(row$f),
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(column$f)),
        matrix$ElementMemoryLayout$F.byteSize());
  }

  public int matrix(long row, long column) {
    return (int) matrix$VarHandle$F.get(MemorySegment$F, row, column);
  }

  public ArrayFieldsFM matrix(
      long row, long column,
      int value$f) {
    matrix$VarHandle$F.set(MemorySegment$F, row, column, value$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      points$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("points");

  public static final java.lang.foreign.MemoryLayout.PathElement
      points$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      points$MemoryLayout$F =
          MemoryLayout$F.select(points$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      points$ElementMemoryLayout$F =
      pkg.ArrayPointFM.MemoryLayout$F;

  public static final long points$Sequence0Dimension$F = 2L;

  public java.lang.foreign.MemorySegment
      pointsAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(points$PathElement$F),
        points$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      pointsAsMemorySegment$F(long index$f) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            points$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        points$ElementMemoryLayout$F.byteSize());
  }

  public pkg.ArrayPoint points(long index) {
    return pkg.ArrayPointFM.fromMemorySegment$F(
        pointsAsMemorySegment$F(index));
  }

  public ArrayFieldsFM points(
      long index,
      pkg.ArrayPoint value$f) {
    pkg.ArrayPointFM.toMemorySegment$F(
        value$f, pointsAsMemorySegment$F(index));
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      pointers$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("pointers");

  public static final java.lang.foreign.MemoryLayout.PathElement
      pointers$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      pointers$MemoryLayout$F =
          MemoryLayout$F.select(pointers$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      pointers$ElementMemoryLayout$F =
      java.lang.foreign.ValueLayout.ADDRESS;

  public static final long pointers$Sequence0Dimension$F = 2L;

  public static final java.lang.invoke.VarHandle pointers$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(
              pointers$PathElement$F, pointers$Sequence0PathElement$F), 1, 0L);

  public java.lang.foreign.MemorySegment
      pointersAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(pointers$PathElement$F),
        pointers$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      pointersAsMemorySegment$F(long index$f) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            pointers$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        pointers$ElementMemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment pointersAsAddress$F(long index$f) {
    return (java.lang.foreign.MemorySegment)
        pointers$VarHandle$F.get(MemorySegment$F, index$f);
  }

  public ArrayFieldsFM pointersAsAddress$F(
      long index$f, java.lang.foreign.MemorySegment value$f) {
    pointers$VarHandle$F.set(MemorySegment$F, index$f,
        value$f == null
            ? java.lang.foreign.MemorySegment.NULL : value$f);
    return this;
  }

  public pkg.ArrayPoint pointers(long index) {
    var address$f = pointersAsAddress$F(index);
    return address$f.address() == 0L
        ? null
        : pkg.ArrayPointFM.reinterpret$F(address$f);
  }

  public ArrayFieldsFM pointers(
      java.lang.foreign.SegmentAllocator allocator$f, long index,
      pkg.ArrayPoint value$f) {
    pointers$VarHandle$F.set(MemorySegment$F, index,
        value$f == null
            ? java.lang.foreign.MemorySegment.NULL
            : pkg.ArrayPointFM.toMemorySegment$F(
                allocator$f, value$f));
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      cells$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("cells");

  public static final java.lang.foreign.MemoryLayout.PathElement
      cells$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      cells$MemoryLayout$F =
          MemoryLayout$F.select(cells$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      cells$ElementMemoryLayout$F =
      pkg.ArrayCellFM.MemoryLayout$F;

  public static final long cells$Sequence0Dimension$F = 2L;

  public java.lang.foreign.MemorySegment
      cellsAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(cells$PathElement$F),
        cells$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      cellsAsMemorySegment$F(long index$f) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            cells$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        cells$ElementMemoryLayout$F.byteSize());
  }

  public pkg.ArrayCell cells(long index) {
    return new pkg.ArrayCellFM(
        cellsAsMemorySegment$F(index));
  }

  public ArrayFieldsFM cells(
      long index,
      pkg.ArrayCell value$f) {
    java.lang.foreign.MemorySegment.copy(
        ((pkg.ArrayCellFM) value$f).MemorySegment$F, 0L,
        cellsAsMemorySegment$F(index), 0L,
        cells$ElementMemoryLayout$F.byteSize());
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      references$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("references");

  public static final java.lang.foreign.MemoryLayout.PathElement
      references$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      references$MemoryLayout$F =
          MemoryLayout$F.select(references$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      references$ElementMemoryLayout$F =
      java.lang.foreign.ValueLayout.ADDRESS;

  public static final long references$Sequence0Dimension$F = 2L;

  public static final java.lang.invoke.VarHandle references$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(
              references$PathElement$F, references$Sequence0PathElement$F), 1, 0L);

  public java.lang.foreign.MemorySegment
      referencesAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(references$PathElement$F),
        references$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      referencesAsMemorySegment$F(long index$f) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            references$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        references$ElementMemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment referencesAsAddress$F(long index$f) {
    return (java.lang.foreign.MemorySegment)
        references$VarHandle$F.get(MemorySegment$F, index$f);
  }

  public ArrayFieldsFM referencesAsAddress$F(
      long index$f, java.lang.foreign.MemorySegment value$f) {
    references$VarHandle$F.set(MemorySegment$F, index$f,
        value$f == null
            ? java.lang.foreign.MemorySegment.NULL : value$f);
    return this;
  }

  public pkg.ArrayCell references(long index) {
    var address$f = referencesAsAddress$F(index);
    return address$f.address() == 0L
        ? null
        : pkg.ArrayCellFM.reinterpret$F(address$f);
  }

  public ArrayFieldsFM references(
      long index,
      pkg.ArrayCell value$f) {
    references$VarHandle$F.set(MemorySegment$F, index,
        value$f == null
            ? java.lang.foreign.MemorySegment.NULL
            : ((pkg.ArrayCellFM) value$f).MemorySegment$F);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      raw$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("raw");

  public static final java.lang.foreign.MemoryLayout.PathElement
      raw$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      raw$MemoryLayout$F =
          MemoryLayout$F.select(raw$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      raw$ElementMemoryLayout$F =
      java.lang.foreign.ValueLayout.ADDRESS;

  public static final long raw$Sequence0Dimension$F = 2L;

  public static final java.lang.invoke.VarHandle raw$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(
              raw$PathElement$F, raw$Sequence0PathElement$F), 1, 0L);

  public java.lang.foreign.MemorySegment
      rawAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(raw$PathElement$F),
        raw$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      rawAsMemorySegment$F(long index$f) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            raw$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        raw$ElementMemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment rawAsAddress$F(long index$f) {
    return (java.lang.foreign.MemorySegment)
        raw$VarHandle$F.get(MemorySegment$F, index$f);
  }

  public ArrayFieldsFM rawAsAddress$F(
      long index$f, java.lang.foreign.MemorySegment value$f) {
    raw$VarHandle$F.set(MemorySegment$F, index$f,
        value$f == null
            ? java.lang.foreign.MemorySegment.NULL : value$f);
    return this;
  }

  public java.lang.foreign.MemorySegment raw(long index) {
    return rawAsAddress$F(index);
  }

  public ArrayFieldsFM raw(
      long index,
      java.lang.foreign.MemorySegment value$f) {
    raw$VarHandle$F.set(MemorySegment$F, index,
        value$f == null
            ? java.lang.foreign.MemorySegment.NULL : value$f);
    return this;
  }
}

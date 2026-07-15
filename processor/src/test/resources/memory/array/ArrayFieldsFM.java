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
      java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      MemoryLayout$F.byteSize(), MemoryLayout$F.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate$F(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(MemoryLayout$F, count);
  }

  public static ArrayFieldsFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new ArrayFieldsFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return memorySegment.reinterpret(Math.multiplyExact(
        MemoryLayout$F.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment elementAt$F(
      java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, MemoryLayout$F.byteSize()), MemoryLayout$F.byteSize());
  }

  public static ArrayFieldsFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new ArrayFieldsFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public ArrayFieldsFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public ArrayFieldsFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
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

  public static final long flags$Sequence0Dimension$F =
      3L;

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
      flagsAsMemorySegment$F(int index0) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            flags$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        flags$ElementMemoryLayout$F.byteSize());
  }

  public boolean flags(int index0$f) {
    return (boolean) flags$VarHandle$F.get(MemorySegment$F, index0$f);
  }

  public ArrayFieldsFM flags(
      int index0$f,
      boolean value$f) {
    flags$VarHandle$F.set(MemorySegment$F, index0$f, value$f);
    return this;
  }

  public java.nio.ByteBuffer flagsAsBuffer$F() {
    return flagsAsMemorySegment$F().asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder());
  }

  public boolean[] flagsToArray$F() {
    var result =
        new boolean[(int) flags$Sequence0Dimension$F];
    for (int index = 0;
        index < result.length; index++) {
      result[(int) index] = flags(index);
    }
    return result;
  }

  public ArrayFieldsFM flagsFromArray$F(boolean[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != flags$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "flags length must be "
              + flags$Sequence0Dimension$F);
    }
    for (int index = 0;
        index < value.length; index++) {
      flags(index, value[(int) index]);
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

  public static final long matrix$Sequence0Dimension$F =
      2L;

  public static final long matrix$Sequence1Dimension$F =
      3L;

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
      matrixAsMemorySegment$F(long index0, long index1) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            matrix$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0),
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index1)),
        matrix$ElementMemoryLayout$F.byteSize());
  }

  public int matrix(long index0$f, long index1$f) {
    return (int) matrix$VarHandle$F.get(MemorySegment$F, index0$f, index1$f);
  }

  public ArrayFieldsFM matrix(
      long index0$f, long index1$f,
      int value$f) {
    matrix$VarHandle$F.set(MemorySegment$F, index0$f, index1$f, value$f);
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

  public static final long points$Sequence0Dimension$F =
      2L;

  public java.lang.foreign.MemorySegment
      pointsAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(points$PathElement$F),
        points$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      pointsAsMemorySegment$F(long index0) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            points$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        points$ElementMemoryLayout$F.byteSize());
  }

  public pkg.ArrayPoint points(long index0$f) {
    return pkg.ArrayPointFM.fromMemorySegment$F(
        pointsAsMemorySegment$F(index0$f));
  }

  public ArrayFieldsFM points(
      long index0$f,
      pkg.ArrayPoint value$f) {
    pkg.ArrayPointFM.toMemorySegment$F(
        value$f, pointsAsMemorySegment$F(index0$f));
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

  public static final long pointers$Sequence0Dimension$F =
      2L;

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
      pointersAsMemorySegment$F(long index0) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            pointers$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        pointers$ElementMemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment pointersAsAddress$F(long index0) {
    return (java.lang.foreign.MemorySegment)
        pointers$VarHandle$F.get(MemorySegment$F, index0);
  }

  public ArrayFieldsFM pointersAsAddress$F(
      long index0, java.lang.foreign.MemorySegment value) {
    pointers$VarHandle$F.set(MemorySegment$F, index0,
        value == null
            ? java.lang.foreign.MemorySegment.NULL : value);
    return this;
  }

  public pkg.ArrayPoint pointers(long index0$f) {
    var address$f = pointersAsAddress$F(index0$f);
    return address$f.address() == 0L
        ? null
        : pkg.ArrayPointFM.reinterpret$F(address$f);
  }

  public ArrayFieldsFM pointers(
      java.lang.foreign.SegmentAllocator allocator$f, long index0$f,
      pkg.ArrayPoint value$f) {
    pointers$VarHandle$F.set(MemorySegment$F, index0$f,
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

  public static final long cells$Sequence0Dimension$F =
      2L;

  public java.lang.foreign.MemorySegment
      cellsAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(cells$PathElement$F),
        cells$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      cellsAsMemorySegment$F(long index0) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            cells$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        cells$ElementMemoryLayout$F.byteSize());
  }

  public pkg.ArrayCell cells(long index0$f) {
    return new pkg.ArrayCellFM(
        cellsAsMemorySegment$F(index0$f));
  }

  public ArrayFieldsFM cells(
      long index0$f,
      pkg.ArrayCell value$f) {
    java.lang.foreign.MemorySegment.copy(
        ((pkg.ArrayCellFM) value$f).MemorySegment$F, 0L,
        cellsAsMemorySegment$F(index0$f), 0L,
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

  public static final long references$Sequence0Dimension$F =
      2L;

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
      referencesAsMemorySegment$F(long index0) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            references$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        references$ElementMemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment referencesAsAddress$F(long index0) {
    return (java.lang.foreign.MemorySegment)
        references$VarHandle$F.get(MemorySegment$F, index0);
  }

  public ArrayFieldsFM referencesAsAddress$F(
      long index0, java.lang.foreign.MemorySegment value) {
    references$VarHandle$F.set(MemorySegment$F, index0,
        value == null
            ? java.lang.foreign.MemorySegment.NULL : value);
    return this;
  }

  public pkg.ArrayCell references(long index0$f) {
    var address$f = referencesAsAddress$F(index0$f);
    return address$f.address() == 0L
        ? null
        : pkg.ArrayCellFM.reinterpret$F(address$f);
  }

  public ArrayFieldsFM references(
      long index0$f,
      pkg.ArrayCell value$f) {
    references$VarHandle$F.set(MemorySegment$F, index0$f,
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

  public static final long raw$Sequence0Dimension$F =
      2L;

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
      rawAsMemorySegment$F(long index0) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            raw$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        raw$ElementMemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment rawAsAddress$F(long index0) {
    return (java.lang.foreign.MemorySegment)
        raw$VarHandle$F.get(MemorySegment$F, index0);
  }

  public ArrayFieldsFM rawAsAddress$F(
      long index0, java.lang.foreign.MemorySegment value) {
    raw$VarHandle$F.set(MemorySegment$F, index0,
        value == null
            ? java.lang.foreign.MemorySegment.NULL : value);
    return this;
  }

  public java.lang.foreign.MemorySegment raw(long index0$f) {
    return rawAsAddress$F(index0$f);
  }

  public ArrayFieldsFM raw(
      long index0$f,
      java.lang.foreign.MemorySegment value$f) {
    raw$VarHandle$F.set(MemorySegment$F, index0$f,
        value$f == null
            ? java.lang.foreign.MemorySegment.NULL : value$f);
    return this;
  }
}

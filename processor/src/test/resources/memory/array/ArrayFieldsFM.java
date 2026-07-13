package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ArrayFieldsFM implements ArrayFields {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.MemoryLayout.sequenceLayout(3L,
            java.lang.foreign.ValueLayout.JAVA_BOOLEAN).withName("flags"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            java.lang.foreign.MemoryLayout.sequenceLayout(3L,
                java.lang.foreign.ValueLayout.JAVA_INT)).withName("matrix"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            pkg.ArrayPointFM.FM$LAYOUT).withName("points"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            java.lang.foreign.ValueLayout.ADDRESS).withName("pointers"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            pkg.ArrayCellFM.FM$LAYOUT).withName("cells"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            java.lang.foreign.ValueLayout.ADDRESS).withName("references"),
        java.lang.foreign.MemoryLayout.sequenceLayout(2L,
            java.lang.foreign.ValueLayout.ADDRESS).withName("raw"),
      }));

  public static java.lang.foreign.MemorySegment allocate(java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static ArrayFieldsFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new ArrayFieldsFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret(
      java.lang.foreign.MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment FM$at(java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static ArrayFieldsFM at(java.lang.foreign.MemorySegment array, long index) {
    return new ArrayFieldsFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public ArrayFieldsFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public ArrayFieldsFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$flags =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("flags");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$flags$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$flags =
      FM$LAYOUT.select(FM$PE$flags);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$flags =
      java.lang.foreign.ValueLayout.JAVA_BOOLEAN;

  public static final long FM$OFFSET$flags =
      FM$LAYOUT.byteOffset(FM$PE$flags);

  public static final long FM$SIZE$flags =
      FM$LAYOUT$flags.byteSize();

  public static final long FM$DIMENSION$flags$0 = 3L;

  public static final java.lang.invoke.VarHandle FM$VH$flags =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$flags, FM$PE$flags$0), 1, 0L);

  public java.lang.foreign.MemorySegment flags$MemorySegment() {
    return ms.asSlice(FM$OFFSET$flags, FM$SIZE$flags);
  }

  public java.lang.foreign.MemorySegment flags$MemorySegment(int index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$flags,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$flags.byteSize());
  }

  public boolean flags(int index) {
    return (boolean) FM$VH$flags.get(ms, index);
  }

  public ArrayFieldsFM flags(
      int index,
      boolean value) {
    FM$VH$flags.set(ms, index, value);
    return this;
  }

  public java.nio.ByteBuffer flags$Buffer() {
    return flags$MemorySegment().asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder());
  }

  public boolean[] flags$Array() {
    var value = new boolean[(int) FM$DIMENSION$flags$0];
    for (int index = 0; index < value.length; index++) {
      value[(int) index] = flags(index);
    }
    return value;
  }

  public ArrayFieldsFM flags(boolean[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != FM$DIMENSION$flags$0) {
      throw new IllegalArgumentException(
          "flags length must be " + FM$DIMENSION$flags$0);
    }
    for (int index = 0; index < value.length; index++) {
      flags(index, value[(int) index]);
    }
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$matrix =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("matrix");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$matrix$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$matrix$1 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$matrix =
      FM$LAYOUT.select(FM$PE$matrix);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$matrix =
      java.lang.foreign.ValueLayout.JAVA_INT;

  public static final long FM$OFFSET$matrix =
      FM$LAYOUT.byteOffset(FM$PE$matrix);

  public static final long FM$SIZE$matrix =
      FM$LAYOUT$matrix.byteSize();

  public static final long FM$DIMENSION$matrix$0 = 2L;

  public static final long FM$DIMENSION$matrix$1 = 3L;

  public static final java.lang.invoke.VarHandle FM$VH$matrix =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$matrix, FM$PE$matrix$0, FM$PE$matrix$1), 1, 0L);

  public java.lang.foreign.MemorySegment matrix$MemorySegment() {
    return ms.asSlice(FM$OFFSET$matrix, FM$SIZE$matrix);
  }

  public java.lang.foreign.MemorySegment matrix$MemorySegment(long row, long column) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$matrix,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(row),
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(column)),
        FM$ELEMENT_LAYOUT$matrix.byteSize());
  }

  public int matrix(long row, long column) {
    return (int) FM$VH$matrix.get(ms, row, column);
  }

  public ArrayFieldsFM matrix(
      long row, long column,
      int value) {
    FM$VH$matrix.set(ms, row, column, value);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$points =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("points");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$points$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$points =
      FM$LAYOUT.select(FM$PE$points);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$points =
      pkg.ArrayPointFM.FM$LAYOUT;

  public static final long FM$OFFSET$points =
      FM$LAYOUT.byteOffset(FM$PE$points);

  public static final long FM$SIZE$points =
      FM$LAYOUT$points.byteSize();

  public static final long FM$DIMENSION$points$0 = 2L;

  public java.lang.foreign.MemorySegment points$MemorySegment() {
    return ms.asSlice(FM$OFFSET$points, FM$SIZE$points);
  }

  public java.lang.foreign.MemorySegment points$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$points,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$points.byteSize());
  }

  public pkg.ArrayPoint points(long index) {
    return pkg.ArrayPointFM.fromMemorySegment(
        points$MemorySegment(index));
  }

  public ArrayFieldsFM points(
      long index,
      pkg.ArrayPoint value) {
    pkg.ArrayPointFM.toMemorySegment(
        value, points$MemorySegment(index));
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$pointers =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("pointers");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$pointers$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$pointers =
      FM$LAYOUT.select(FM$PE$pointers);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$pointers =
      java.lang.foreign.ValueLayout.ADDRESS;

  public static final long FM$OFFSET$pointers =
      FM$LAYOUT.byteOffset(FM$PE$pointers);

  public static final long FM$SIZE$pointers =
      FM$LAYOUT$pointers.byteSize();

  public static final long FM$DIMENSION$pointers$0 = 2L;

  public static final java.lang.invoke.VarHandle FM$VH$pointers =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$pointers, FM$PE$pointers$0), 1, 0L);

  public java.lang.foreign.MemorySegment pointers$MemorySegment() {
    return ms.asSlice(FM$OFFSET$pointers, FM$SIZE$pointers);
  }

  public java.lang.foreign.MemorySegment pointers$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$pointers,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$pointers.byteSize());
  }

  public java.lang.foreign.MemorySegment pointers$Address(long index) {
    return (java.lang.foreign.MemorySegment) FM$VH$pointers.get(ms, index);
  }

  public ArrayFieldsFM pointers$Address(
      long index, java.lang.foreign.MemorySegment value) {
    FM$VH$pointers.set(ms, index,
        value == null ? java.lang.foreign.MemorySegment.NULL : value);
    return this;
  }

  public pkg.ArrayPoint pointers(long index) {
    var address = pointers$Address(index);
    return address.address() == 0L
        ? null
        : pkg.ArrayPointFM.reinterpret(address);
  }

  public ArrayFieldsFM pointers(
      java.lang.foreign.SegmentAllocator allocator, long index,
      pkg.ArrayPoint value) {
    FM$VH$pointers.set(ms, index,
        value == null
            ? java.lang.foreign.MemorySegment.NULL
            : pkg.ArrayPointFM.toMemorySegment(
                allocator, value));
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$cells =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("cells");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$cells$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$cells =
      FM$LAYOUT.select(FM$PE$cells);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$cells =
      pkg.ArrayCellFM.FM$LAYOUT;

  public static final long FM$OFFSET$cells =
      FM$LAYOUT.byteOffset(FM$PE$cells);

  public static final long FM$SIZE$cells =
      FM$LAYOUT$cells.byteSize();

  public static final long FM$DIMENSION$cells$0 = 2L;

  public java.lang.foreign.MemorySegment cells$MemorySegment() {
    return ms.asSlice(FM$OFFSET$cells, FM$SIZE$cells);
  }

  public java.lang.foreign.MemorySegment cells$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$cells,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$cells.byteSize());
  }

  public pkg.ArrayCell cells(long index) {
    return new pkg.ArrayCellFM(
        cells$MemorySegment(index));
  }

  public ArrayFieldsFM cells(
      long index,
      pkg.ArrayCell value) {
    java.lang.foreign.MemorySegment.copy(
        ((pkg.ArrayCellFM) value).ms, 0L,
        cells$MemorySegment(index), 0L,
        FM$ELEMENT_LAYOUT$cells.byteSize());
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$references =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("references");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$references$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$references =
      FM$LAYOUT.select(FM$PE$references);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$references =
      java.lang.foreign.ValueLayout.ADDRESS;

  public static final long FM$OFFSET$references =
      FM$LAYOUT.byteOffset(FM$PE$references);

  public static final long FM$SIZE$references =
      FM$LAYOUT$references.byteSize();

  public static final long FM$DIMENSION$references$0 = 2L;

  public static final java.lang.invoke.VarHandle FM$VH$references =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$references, FM$PE$references$0), 1, 0L);

  public java.lang.foreign.MemorySegment references$MemorySegment() {
    return ms.asSlice(FM$OFFSET$references, FM$SIZE$references);
  }

  public java.lang.foreign.MemorySegment references$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$references,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$references.byteSize());
  }

  public java.lang.foreign.MemorySegment references$Address(long index) {
    return (java.lang.foreign.MemorySegment) FM$VH$references.get(ms, index);
  }

  public ArrayFieldsFM references$Address(
      long index, java.lang.foreign.MemorySegment value) {
    FM$VH$references.set(ms, index,
        value == null ? java.lang.foreign.MemorySegment.NULL : value);
    return this;
  }

  public pkg.ArrayCell references(long index) {
    var address = references$Address(index);
    return address.address() == 0L
        ? null
        : pkg.ArrayCellFM.reinterpret(address);
  }

  public ArrayFieldsFM references(
      long index,
      pkg.ArrayCell value) {
    FM$VH$references.set(ms, index,
        value == null
            ? java.lang.foreign.MemorySegment.NULL
            : ((pkg.ArrayCellFM) value).ms);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$raw =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("raw");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$raw$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$raw =
      FM$LAYOUT.select(FM$PE$raw);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$raw =
      java.lang.foreign.ValueLayout.ADDRESS;

  public static final long FM$OFFSET$raw =
      FM$LAYOUT.byteOffset(FM$PE$raw);

  public static final long FM$SIZE$raw =
      FM$LAYOUT$raw.byteSize();

  public static final long FM$DIMENSION$raw$0 = 2L;

  public static final java.lang.invoke.VarHandle FM$VH$raw =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$raw, FM$PE$raw$0), 1, 0L);

  public java.lang.foreign.MemorySegment raw$MemorySegment() {
    return ms.asSlice(FM$OFFSET$raw, FM$SIZE$raw);
  }

  public java.lang.foreign.MemorySegment raw$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$raw,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$raw.byteSize());
  }

  public java.lang.foreign.MemorySegment raw$Address(long index) {
    return (java.lang.foreign.MemorySegment) FM$VH$raw.get(ms, index);
  }

  public ArrayFieldsFM raw$Address(
      long index, java.lang.foreign.MemorySegment value) {
    FM$VH$raw.set(ms, index,
        value == null ? java.lang.foreign.MemorySegment.NULL : value);
    return this;
  }

  public java.lang.foreign.MemorySegment raw(long index) {
    return raw$Address(index);
  }

  public ArrayFieldsFM raw(
      long index,
      java.lang.foreign.MemorySegment value) {
    FM$VH$raw.set(ms, index,
        value == null ? java.lang.foreign.MemorySegment.NULL : value);
    return this;
  }
}

package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ArrayFieldsFM implements ArrayFields {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        MemoryLayout.sequenceLayout(3L,
            ValueLayout.JAVA_BOOLEAN).withName("flags"),
        MemoryLayout.sequenceLayout(2L,
            MemoryLayout.sequenceLayout(3L,
                ValueLayout.JAVA_INT)).withName("matrix"),
        MemoryLayout.sequenceLayout(2L,
            pkg.ArrayPointFM.FM$LAYOUT).withName("points"),
        MemoryLayout.sequenceLayout(2L,
            ValueLayout.ADDRESS).withName("pointers"),
        MemoryLayout.sequenceLayout(2L,
            pkg.ArrayCellFM.FM$LAYOUT).withName("cells"),
        MemoryLayout.sequenceLayout(2L,
            ValueLayout.ADDRESS).withName("references"),
        MemoryLayout.sequenceLayout(2L,
            ValueLayout.ADDRESS).withName("raw"),
      }));

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static MemorySegment allocate(
      SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static ArrayFieldsFM reinterpret(MemorySegment ms) {
    return new ArrayFieldsFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static MemorySegment reinterpret(
      MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static MemorySegment FM$at(MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static ArrayFieldsFM at(MemorySegment array, long index) {
    return new ArrayFieldsFM(FM$at(array, index));
  }

  public final MemorySegment ms;

  public ArrayFieldsFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public ArrayFieldsFM(MemorySegment ms) {
    this.ms = ms;
  }

  public static final MemoryLayout.PathElement FM$PE$flags =
      MemoryLayout.PathElement.groupElement("flags");

  public static final MemoryLayout.PathElement FM$PE$flags$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$flags =
      FM$LAYOUT.select(FM$PE$flags);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$flags =
      ValueLayout.JAVA_BOOLEAN;

  public static final long FM$OFFSET$flags =
      FM$LAYOUT.byteOffset(FM$PE$flags);

  public static final long FM$SIZE$flags =
      FM$LAYOUT$flags.byteSize();

  public static final long FM$DIMENSION$flags$0 = 3L;

  public static final java.lang.invoke.VarHandle FM$VH$flags =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$flags, FM$PE$flags$0), 1, 0L);

  public MemorySegment flags$MemorySegment() {
    return ms.asSlice(FM$OFFSET$flags, FM$SIZE$flags);
  }

  public MemorySegment flags$MemorySegment(int index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$flags,
            MemoryLayout.PathElement.sequenceElement(index)),
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

  public static final MemoryLayout.PathElement FM$PE$matrix =
      MemoryLayout.PathElement.groupElement("matrix");

  public static final MemoryLayout.PathElement FM$PE$matrix$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout.PathElement FM$PE$matrix$1 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$matrix =
      FM$LAYOUT.select(FM$PE$matrix);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$matrix =
      ValueLayout.JAVA_INT;

  public static final long FM$OFFSET$matrix =
      FM$LAYOUT.byteOffset(FM$PE$matrix);

  public static final long FM$SIZE$matrix =
      FM$LAYOUT$matrix.byteSize();

  public static final long FM$DIMENSION$matrix$0 = 2L;

  public static final long FM$DIMENSION$matrix$1 = 3L;

  public static final java.lang.invoke.VarHandle FM$VH$matrix =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$matrix, FM$PE$matrix$0, FM$PE$matrix$1), 1, 0L);

  public MemorySegment matrix$MemorySegment() {
    return ms.asSlice(FM$OFFSET$matrix, FM$SIZE$matrix);
  }

  public MemorySegment matrix$MemorySegment(long row, long column) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$matrix,
            MemoryLayout.PathElement.sequenceElement(row),
            MemoryLayout.PathElement.sequenceElement(column)),
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

  public static final MemoryLayout.PathElement FM$PE$points =
      MemoryLayout.PathElement.groupElement("points");

  public static final MemoryLayout.PathElement FM$PE$points$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$points =
      FM$LAYOUT.select(FM$PE$points);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$points =
      pkg.ArrayPointFM.FM$LAYOUT;

  public static final long FM$OFFSET$points =
      FM$LAYOUT.byteOffset(FM$PE$points);

  public static final long FM$SIZE$points =
      FM$LAYOUT$points.byteSize();

  public static final long FM$DIMENSION$points$0 = 2L;

  public MemorySegment points$MemorySegment() {
    return ms.asSlice(FM$OFFSET$points, FM$SIZE$points);
  }

  public MemorySegment points$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$points,
            MemoryLayout.PathElement.sequenceElement(index)),
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

  public static final MemoryLayout.PathElement FM$PE$pointers =
      MemoryLayout.PathElement.groupElement("pointers");

  public static final MemoryLayout.PathElement FM$PE$pointers$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$pointers =
      FM$LAYOUT.select(FM$PE$pointers);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$pointers =
      ValueLayout.ADDRESS;

  public static final long FM$OFFSET$pointers =
      FM$LAYOUT.byteOffset(FM$PE$pointers);

  public static final long FM$SIZE$pointers =
      FM$LAYOUT$pointers.byteSize();

  public static final long FM$DIMENSION$pointers$0 = 2L;

  public static final java.lang.invoke.VarHandle FM$VH$pointers =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$pointers, FM$PE$pointers$0), 1, 0L);

  public MemorySegment pointers$MemorySegment() {
    return ms.asSlice(FM$OFFSET$pointers, FM$SIZE$pointers);
  }

  public MemorySegment pointers$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$pointers,
            MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$pointers.byteSize());
  }

  public MemorySegment pointers$Address(long index) {
    return (MemorySegment) FM$VH$pointers.get(ms, index);
  }

  public ArrayFieldsFM pointers$Address(
      long index, MemorySegment value) {
    FM$VH$pointers.set(ms, index,
        value == null ? MemorySegment.NULL : value);
    return this;
  }

  public pkg.ArrayPoint pointers(long index) {
    var address = pointers$Address(index);
    return address.address() == 0L
        ? null
        : pkg.ArrayPointFM.reinterpret(address);
  }

  public ArrayFieldsFM pointers(
      SegmentAllocator allocator, long index,
      pkg.ArrayPoint value) {
    FM$VH$pointers.set(ms, index,
        value == null
            ? MemorySegment.NULL
            : pkg.ArrayPointFM.toMemorySegment(
                allocator, value));
    return this;
  }

  public static final MemoryLayout.PathElement FM$PE$cells =
      MemoryLayout.PathElement.groupElement("cells");

  public static final MemoryLayout.PathElement FM$PE$cells$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$cells =
      FM$LAYOUT.select(FM$PE$cells);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$cells =
      pkg.ArrayCellFM.FM$LAYOUT;

  public static final long FM$OFFSET$cells =
      FM$LAYOUT.byteOffset(FM$PE$cells);

  public static final long FM$SIZE$cells =
      FM$LAYOUT$cells.byteSize();

  public static final long FM$DIMENSION$cells$0 = 2L;

  public MemorySegment cells$MemorySegment() {
    return ms.asSlice(FM$OFFSET$cells, FM$SIZE$cells);
  }

  public MemorySegment cells$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$cells,
            MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$cells.byteSize());
  }

  public pkg.ArrayCell cells(long index) {
    return new pkg.ArrayCellFM(
        cells$MemorySegment(index));
  }

  public ArrayFieldsFM cells(
      long index,
      pkg.ArrayCell value) {
    MemorySegment.copy(
        ((pkg.ArrayCellFM) value).ms, 0L,
        cells$MemorySegment(index), 0L,
        FM$ELEMENT_LAYOUT$cells.byteSize());
    return this;
  }

  public static final MemoryLayout.PathElement FM$PE$references =
      MemoryLayout.PathElement.groupElement("references");

  public static final MemoryLayout.PathElement FM$PE$references$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$references =
      FM$LAYOUT.select(FM$PE$references);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$references =
      ValueLayout.ADDRESS;

  public static final long FM$OFFSET$references =
      FM$LAYOUT.byteOffset(FM$PE$references);

  public static final long FM$SIZE$references =
      FM$LAYOUT$references.byteSize();

  public static final long FM$DIMENSION$references$0 = 2L;

  public static final java.lang.invoke.VarHandle FM$VH$references =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$references, FM$PE$references$0), 1, 0L);

  public MemorySegment references$MemorySegment() {
    return ms.asSlice(FM$OFFSET$references, FM$SIZE$references);
  }

  public MemorySegment references$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$references,
            MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$references.byteSize());
  }

  public MemorySegment references$Address(long index) {
    return (MemorySegment) FM$VH$references.get(ms, index);
  }

  public ArrayFieldsFM references$Address(
      long index, MemorySegment value) {
    FM$VH$references.set(ms, index,
        value == null ? MemorySegment.NULL : value);
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
            ? MemorySegment.NULL
            : ((pkg.ArrayCellFM) value).ms);
    return this;
  }

  public static final MemoryLayout.PathElement FM$PE$raw =
      MemoryLayout.PathElement.groupElement("raw");

  public static final MemoryLayout.PathElement FM$PE$raw$0 =
      MemoryLayout.PathElement.sequenceElement();

  public static final MemoryLayout FM$LAYOUT$raw =
      FM$LAYOUT.select(FM$PE$raw);

  public static final MemoryLayout FM$ELEMENT_LAYOUT$raw =
      ValueLayout.ADDRESS;

  public static final long FM$OFFSET$raw =
      FM$LAYOUT.byteOffset(FM$PE$raw);

  public static final long FM$SIZE$raw =
      FM$LAYOUT$raw.byteSize();

  public static final long FM$DIMENSION$raw$0 = 2L;

  public static final java.lang.invoke.VarHandle FM$VH$raw =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$raw, FM$PE$raw$0), 1, 0L);

  public MemorySegment raw$MemorySegment() {
    return ms.asSlice(FM$OFFSET$raw, FM$SIZE$raw);
  }

  public MemorySegment raw$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$raw,
            MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$raw.byteSize());
  }

  public MemorySegment raw$Address(long index) {
    return (MemorySegment) FM$VH$raw.get(ms, index);
  }

  public ArrayFieldsFM raw$Address(
      long index, MemorySegment value) {
    FM$VH$raw.set(ms, index,
        value == null ? MemorySegment.NULL : value);
    return this;
  }

  public java.lang.foreign.MemorySegment raw(long index) {
    return raw$Address(index);
  }

  public ArrayFieldsFM raw(
      long index,
      java.lang.foreign.MemorySegment value) {
    FM$VH$raw.set(ms, index,
        value == null ? MemorySegment.NULL : value);
    return this;
  }
}

package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ArrayUnionFM implements ArrayUnion {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.unionLayout(
          org.alveolo.ffm.ForeignUtils.unionPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.MemoryLayout.sequenceLayout(4L,
            java.lang.foreign.ValueLayout.JAVA_SHORT).withName("words"),
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

  public static ArrayUnionFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new ArrayUnionFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static ArrayUnionFM at(java.lang.foreign.MemorySegment array, long index) {
    return new ArrayUnionFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public ArrayUnionFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public ArrayUnionFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$words =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("words");

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$words$0 =
      java.lang.foreign.MemoryLayout.PathElement.sequenceElement();

  public static final java.lang.foreign.MemoryLayout FM$LAYOUT$words =
      FM$LAYOUT.select(FM$PE$words);

  public static final java.lang.foreign.MemoryLayout FM$ELEMENT_LAYOUT$words =
      java.lang.foreign.ValueLayout.JAVA_SHORT;

  public static final long FM$OFFSET$words =
      FM$LAYOUT.byteOffset(FM$PE$words);

  public static final long FM$SIZE$words =
      FM$LAYOUT$words.byteSize();

  public static final long FM$DIMENSION$words$0 = 4L;

  public static final java.lang.invoke.VarHandle FM$VH$words =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$words, FM$PE$words$0), 1, 0L);

  public java.lang.foreign.MemorySegment words$MemorySegment() {
    return ms.asSlice(FM$OFFSET$words, FM$SIZE$words);
  }

  public java.lang.foreign.MemorySegment words$MemorySegment(long index) {
    return ms.asSlice(
        FM$LAYOUT.byteOffset(
            FM$PE$words,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index)),
        FM$ELEMENT_LAYOUT$words.byteSize());
  }

  public short words(long index) {
    return (short) FM$VH$words.get(ms, index);
  }

  public ArrayUnionFM words(
      long index,
      short value) {
    FM$VH$words.set(ms, index, value);
    return this;
  }

  public java.nio.ShortBuffer words$Buffer() {
    return words$MemorySegment().asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder()).asShortBuffer();
  }

  public short[] words$Array() {
    var value = new short[(int) FM$DIMENSION$words$0];
    for (long index = 0; index < value.length; index++) {
      value[(int) index] = words(index);
    }
    return value;
  }

  public ArrayUnionFM words(short[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != FM$DIMENSION$words$0) {
      throw new IllegalArgumentException(
          "words length must be " + FM$DIMENSION$words$0);
    }
    for (long index = 0; index < value.length; index++) {
      words(index, value[(int) index]);
    }
    return this;
  }
}

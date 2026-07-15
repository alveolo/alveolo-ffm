package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ArrayUnionFM implements ArrayUnion {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.unionLayout(
          org.alveolo.ffm.ForeignUtils.unionPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.MemoryLayout.sequenceLayout(4L,
            java.lang.foreign.ValueLayout.JAVA_SHORT).withName("words"),
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

  public static ArrayUnionFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new ArrayUnionFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static ArrayUnionFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new ArrayUnionFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public ArrayUnionFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public ArrayUnionFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      words$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("words");

  public static final java.lang.foreign.MemoryLayout.PathElement
      words$Sequence0PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .sequenceElement();

  public static final java.lang.foreign.MemoryLayout
      words$MemoryLayout$F =
          MemoryLayout$F.select(words$PathElement$F);

  public static final java.lang.foreign.MemoryLayout
      words$ElementMemoryLayout$F =
      java.lang.foreign.ValueLayout.JAVA_SHORT;

  public static final long words$Sequence0Dimension$F =
      4L;

  public static final java.lang.invoke.VarHandle words$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(
              words$PathElement$F, words$Sequence0PathElement$F), 1, 0L);

  public java.lang.foreign.MemorySegment
      wordsAsMemorySegment$F() {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(words$PathElement$F),
        words$MemoryLayout$F.byteSize());
  }

  public java.lang.foreign.MemorySegment
      wordsAsMemorySegment$F(long index0) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            words$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index0)),
        words$ElementMemoryLayout$F.byteSize());
  }

  public short words(long index0$f) {
    return (short) words$VarHandle$F.get(MemorySegment$F, index0$f);
  }

  public ArrayUnionFM words(
      long index0$f,
      short value$f) {
    words$VarHandle$F.set(MemorySegment$F, index0$f, value$f);
    return this;
  }

  public java.nio.ShortBuffer wordsAsBuffer$F() {
    return wordsAsMemorySegment$F().asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder()).asShortBuffer();
  }

  public short[] wordsToArray$F() {
    var result =
        new short[(int) words$Sequence0Dimension$F];
    for (long index = 0;
        index < result.length; index++) {
      result[(int) index] = words(index);
    }
    return result;
  }

  public ArrayUnionFM wordsFromArray$F(short[] value) {
    java.util.Objects.requireNonNull(value, "value");
    if (value.length != words$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "words length must be "
              + words$Sequence0Dimension$F);
    }
    for (long index = 0;
        index < value.length; index++) {
      words(index, value[(int) index]);
    }
    return this;
  }
}

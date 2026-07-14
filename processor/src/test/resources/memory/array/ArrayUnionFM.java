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

  public static ArrayUnionFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new ArrayUnionFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static ArrayUnionFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new ArrayUnionFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public ArrayUnionFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public ArrayUnionFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
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

  public static final long words$Sequence0Dimension$F = 4L;

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
      wordsAsMemorySegment$F(long index$f) {
    return MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(
            words$PathElement$F,
            java.lang.foreign.MemoryLayout.PathElement.sequenceElement(index$f)),
        words$ElementMemoryLayout$F.byteSize());
  }

  public short words(long index) {
    return (short) words$VarHandle$F.get(MemorySegment$F, index);
  }

  public ArrayUnionFM words(
      long index,
      short value$f) {
    words$VarHandle$F.set(MemorySegment$F, index, value$f);
    return this;
  }

  public java.nio.ShortBuffer wordsAsBuffer$F() {
    return wordsAsMemorySegment$F().asByteBuffer()
        .order(java.nio.ByteOrder.nativeOrder()).asShortBuffer();
  }

  public short[] wordsToArray$F() {
    var result$f =
        new short[(int) words$Sequence0Dimension$F];
    for (long index$f = 0;
        index$f < result$f.length; index$f++) {
      result$f[(int) index$f] = words(index$f);
    }
    return result$f;
  }

  public ArrayUnionFM wordsFromArray$F(short[] value$f) {
    java.util.Objects.requireNonNull(value$f, "value");
    if (value$f.length != words$Sequence0Dimension$F) {
      throw new IllegalArgumentException(
          "words length must be " + words$Sequence0Dimension$F);
    }
    for (long index$f = 0;
        index$f < value$f.length; index$f++) {
      words(index$f, value$f[(int) index$f]);
    }
    return this;
  }
}

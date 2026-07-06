package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class BufferStructFM implements BufferStruct {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        MemoryLayout.sequenceLayout(3L, ValueLayout.JAVA_INT).withName("data"),
      }));

  public static final MemoryLayout.PathElement FM$PE$data =
      MemoryLayout.PathElement.groupElement("data");

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static BufferStructFM reinterpret(MemorySegment ms) {
    return new BufferStructFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public BufferStructFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public BufferStructFM(MemorySegment ms) {
    this.ms = ms;
  }

  public static final long FM$OFFSET$data =
      FM$LAYOUT.byteOffset(FM$PE$data);

  public static final long FM$SIZE$data =
      FM$LAYOUT.select(FM$PE$data).byteSize();

  private MemorySegment FM$MS$data;

  public MemorySegment data$MemorySegment() {
    if (FM$MS$data == null) {
      FM$MS$data = ms.asSlice(FM$OFFSET$data, FM$SIZE$data);
    }
    return FM$MS$data;
  }

  private java.nio.IntBuffer FM$BB$data;

  public java.nio.IntBuffer data() {
    if (FM$BB$data == null) {
      FM$BB$data = data$MemorySegment().asByteBuffer().asIntBuffer();
    }
    return FM$BB$data;
  }

  /** get element at index */
  public int data(int index) {
    return data$MemorySegment()
      .getAtIndex(ValueLayout.JAVA_INT, index);
  }

  /** set element at index */
  public void data(int index, int value) {
    data$MemorySegment()
      .setAtIndex(ValueLayout.JAVA_INT, index, value);
  }

  /** replace values from array */
  public void data(int[] value) {
    if (value.length != 3) {
      throw new IllegalArgumentException();
    }
    MemorySegment.copy(value, 0,
        data$MemorySegment(), ValueLayout.JAVA_INT, 0, 3);
  }
}

package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public class VtableBaseFM implements VtableBase {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.ADDRESS.withName("vtable$F"),
      }));

  public static final java.lang.foreign.MemoryLayout.PathElement
      vtable$F$PathElement$F =
          java.lang.foreign.MemoryLayout.PathElement
              .groupElement("vtable$F");

  public static final java.lang.invoke.VarHandle
      vtable$F$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(vtable$F$PathElement$F), 1, 0L);

  public static VtableBaseFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : new VtableBaseFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static VtableBaseFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new VtableBaseFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public VtableBaseFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
    var vtable$f = (java.lang.foreign.MemorySegment)
        vtable$F$VarHandle$F.get(MemorySegment$F);
    if (vtable$f.equals(java.lang.foreign.MemorySegment.NULL)) {
      throw new IllegalArgumentException("Object has a NULL vtable");
    }
  }
}

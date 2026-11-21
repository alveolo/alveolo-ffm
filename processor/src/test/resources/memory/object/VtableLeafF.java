package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
class VtableLeafF extends pkg.VtableMidF implements VtableLeaf {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        pkg.VtableMidF.MemoryLayout$F,
      }));

  public static VtableLeafF reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : new VtableLeafF(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpretSequence$F(
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

  public static VtableLeafF at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new VtableLeafF(elementAt$F(array, index));
  }

  private final VtableLeafVF Vtable$F;

  public VtableLeafF(java.lang.foreign.MemorySegment memorySegment) {
    super(memorySegment);
    var vtable$f = (java.lang.foreign.MemorySegment)
        vtable$F$VarHandle$F.get(MemorySegment$F);
    if (vtable$f.equals(java.lang.foreign.MemorySegment.NULL)) {
      throw new IllegalArgumentException("Object has a NULL vtable");
    }
    this.Vtable$F = VtableLeafVF.reinterpret$F(vtable$f);
  }

  private VtableLeafVF Vtable$F() {
    return Vtable$F;
  }

  public int second(
      ) {
    return Vtable$F().second(this);
  }
}

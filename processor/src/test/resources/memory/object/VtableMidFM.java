package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public class VtableMidFM extends pkg.VtableBaseFM implements VtableMid {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        pkg.VtableBaseFM.MemoryLayout$F,
      }));

  public static VtableMidFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return memorySegment.equals(java.lang.foreign.MemorySegment.NULL)
        ? null : new VtableMidFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static VtableMidFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new VtableMidFM(elementAt$F(array, index));
  }

  private final VtableMidVtblFD Vtable$F;

  public VtableMidFM(java.lang.foreign.MemorySegment memorySegment) {
    super(memorySegment);
    var vtable$f = (java.lang.foreign.MemorySegment)
        vtable$F$VarHandle$F.get(MemorySegment$F);
    if (vtable$f.equals(java.lang.foreign.MemorySegment.NULL)) {
      throw new IllegalArgumentException("Object has a NULL vtable");
    }
    this.Vtable$F = VtableMidVtblFD.reinterpret$F(vtable$f);
  }

  private VtableMidVtblFD Vtable$F() {
    return Vtable$F;
  }

  public int first(
      ) {
    return Vtable$F().first(this);
  }
}

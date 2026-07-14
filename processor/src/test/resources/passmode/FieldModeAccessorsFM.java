package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class FieldModeAccessorsFM implements FieldModeAccessors {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        passmode.InnerRecordFM.MemoryLayout$F.withName("recordDefault"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("interfaceDefault"),
        passmode.InnerInterfaceFM.MemoryLayout$F.withName("interfaceTypeUseValue"),
        passmode.TypeAddressRecordFM.MemoryLayout$F.withName("fieldOverridesTypeAddress"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("fieldOverridesTypeValue"),
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

  public static FieldModeAccessorsFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new FieldModeAccessorsFM(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static FieldModeAccessorsFM at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return new FieldModeAccessorsFM(elementAt$F(array$f, index$f));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public FieldModeAccessorsFM(java.lang.foreign.SegmentAllocator allocator$f) {
    this(allocate$F(allocator$f));
  }

  public FieldModeAccessorsFM(java.lang.foreign.MemorySegment memorySegment$f) {
    this.MemorySegment$F = memorySegment$f;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      recordDefault$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("recordDefault");

  public passmode.InnerRecord recordDefault() {
    return passmode.InnerRecordFM.fromMemorySegment$F(MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(recordDefault$PathElement$F),
        MemoryLayout$F.select(recordDefault$PathElement$F).byteSize()));
  }

  public FieldModeAccessorsFM recordDefault(passmode.InnerRecord value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(recordDefault$PathElement$F);
    var slice$f = MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(recordDefault$PathElement$F),
        memoryLayout$f.byteSize());
    passmode.InnerRecordFM.toMemorySegment$F(
        value$f, slice$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      interfaceDefault$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("interfaceDefault");

  public static final java.lang.invoke.VarHandle interfaceDefault$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(interfaceDefault$PathElement$F), 1, 0L);

  public passmode.InnerInterface interfaceDefault() {
    return passmode.InnerInterfaceFM.reinterpret$F((java.lang.foreign.MemorySegment) interfaceDefault$VarHandle$F.get(MemorySegment$F));
  }

  public FieldModeAccessorsFM interfaceDefault(passmode.InnerInterface value$f) {
    interfaceDefault$VarHandle$F.set(MemorySegment$F, ((passmode.InnerInterfaceFM)value$f).MemorySegment$F);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      interfaceTypeUseValue$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("interfaceTypeUseValue");

  public passmode.InnerInterface interfaceTypeUseValue() {
    return new passmode.InnerInterfaceFM(MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(interfaceTypeUseValue$PathElement$F),
        MemoryLayout$F.select(interfaceTypeUseValue$PathElement$F).byteSize()));
  }

  public FieldModeAccessorsFM interfaceTypeUseValue(passmode.InnerInterface value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(interfaceTypeUseValue$PathElement$F);
    var slice$f = MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(interfaceTypeUseValue$PathElement$F),
        memoryLayout$f.byteSize());
    java.lang.foreign.MemorySegment.copy(
        ((passmode.InnerInterfaceFM)value$f).MemorySegment$F, 0,
        slice$f, 0, memoryLayout$f.byteSize());
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      fieldOverridesTypeAddress$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("fieldOverridesTypeAddress");

  public passmode.TypeAddressRecord fieldOverridesTypeAddress() {
    return passmode.TypeAddressRecordFM.fromMemorySegment$F(MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(fieldOverridesTypeAddress$PathElement$F),
        MemoryLayout$F.select(fieldOverridesTypeAddress$PathElement$F).byteSize()));
  }

  public FieldModeAccessorsFM fieldOverridesTypeAddress(passmode.TypeAddressRecord value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(fieldOverridesTypeAddress$PathElement$F);
    var slice$f = MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(fieldOverridesTypeAddress$PathElement$F),
        memoryLayout$f.byteSize());
    passmode.TypeAddressRecordFM.toMemorySegment$F(
        value$f, slice$f);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      fieldOverridesTypeValue$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("fieldOverridesTypeValue");

  public static final java.lang.invoke.VarHandle fieldOverridesTypeValue$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(fieldOverridesTypeValue$PathElement$F), 1, 0L);

  public passmode.TypeValueInterface fieldOverridesTypeValue() {
    return passmode.TypeValueInterfaceFM.reinterpret$F((java.lang.foreign.MemorySegment) fieldOverridesTypeValue$VarHandle$F.get(MemorySegment$F));
  }

  public FieldModeAccessorsFM fieldOverridesTypeValue(passmode.TypeValueInterface value$f) {
    fieldOverridesTypeValue$VarHandle$F.set(MemorySegment$F, ((passmode.TypeValueInterfaceFM)value$f).MemorySegment$F);
    return this;
  }
}

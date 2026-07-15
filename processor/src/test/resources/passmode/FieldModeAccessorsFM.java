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

  public static FieldModeAccessorsFM reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new FieldModeAccessorsFM(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static FieldModeAccessorsFM at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return new FieldModeAccessorsFM(elementAt$F(array, index));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public FieldModeAccessorsFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate$F(allocator));
  }

  public FieldModeAccessorsFM(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      recordDefault$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("recordDefault");

  public passmode.InnerRecord recordDefault() {
    return passmode.InnerRecordFM.fromMemorySegment$F(MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(recordDefault$PathElement$F),
        MemoryLayout$F.select(recordDefault$PathElement$F).byteSize()));
  }

  public FieldModeAccessorsFM recordDefault(passmode.InnerRecord value) {
    var memoryLayout =
        MemoryLayout$F.select(recordDefault$PathElement$F);
    var slice = MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(recordDefault$PathElement$F),
        memoryLayout.byteSize());
    passmode.InnerRecordFM.toMemorySegment$F(
        value, slice);
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

  public FieldModeAccessorsFM interfaceDefault(passmode.InnerInterface value) {
    interfaceDefault$VarHandle$F.set(MemorySegment$F, ((passmode.InnerInterfaceFM) value).MemorySegment$F);
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

  public FieldModeAccessorsFM interfaceTypeUseValue(passmode.InnerInterface value) {
    var memoryLayout =
        MemoryLayout$F.select(interfaceTypeUseValue$PathElement$F);
    var slice = MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(interfaceTypeUseValue$PathElement$F),
        memoryLayout.byteSize());
    java.lang.foreign.MemorySegment.copy(
        ((passmode.InnerInterfaceFM) value).MemorySegment$F, 0,
        slice, 0, memoryLayout.byteSize());
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

  public FieldModeAccessorsFM fieldOverridesTypeAddress(passmode.TypeAddressRecord value) {
    var memoryLayout =
        MemoryLayout$F.select(fieldOverridesTypeAddress$PathElement$F);
    var slice = MemorySegment$F.asSlice(
        MemoryLayout$F.byteOffset(fieldOverridesTypeAddress$PathElement$F),
        memoryLayout.byteSize());
    passmode.TypeAddressRecordFM.toMemorySegment$F(
        value, slice);
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

  public FieldModeAccessorsFM fieldOverridesTypeValue(passmode.TypeValueInterface value) {
    fieldOverridesTypeValue$VarHandle$F.set(MemorySegment$F, ((passmode.TypeValueInterfaceFM) value).MemorySegment$F);
    return this;
  }
}

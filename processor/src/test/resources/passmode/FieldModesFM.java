package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class FieldModesFM {
  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(
              new java.lang.foreign.MemoryLayout [] {
        passmode.InnerRecordFM.MemoryLayout$F.withName("recordDefault"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("interfaceDefault"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("recordTypeUseAddress"),
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

  public static FieldModes reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return fromMemorySegment$F(memorySegment.reinterpret(MemoryLayout$F.byteSize()));
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

  public static FieldModes at$F(
      java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment$F(elementAt$F(array, index));
  }

  public static void toMemorySegment$F(
      FieldModes source,
      java.lang.foreign.MemorySegment memorySegment,
      java.lang.foreign.SegmentAllocator allocator) {
    recordDefault(memorySegment, source.recordDefault());
    interfaceDefault(memorySegment, source.interfaceDefault());
    recordTypeUseAddress(memorySegment, allocator, source.recordTypeUseAddress());
    interfaceTypeUseValue(memorySegment, source.interfaceTypeUseValue());
    fieldOverridesTypeAddress(memorySegment, source.fieldOverridesTypeAddress());
    fieldOverridesTypeValue(memorySegment, source.fieldOverridesTypeValue());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator,
      FieldModes source) {
    var memorySegment = allocate$F(allocator);
    toMemorySegment$F(source, memorySegment, allocator);
    return memorySegment;
  }

  public static FieldModes fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new FieldModes(
        recordDefault(memorySegment),
        interfaceDefault(memorySegment),
        recordTypeUseAddress(memorySegment),
        interfaceTypeUseValue(memorySegment),
        fieldOverridesTypeAddress(memorySegment),
        fieldOverridesTypeValue(memorySegment));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      recordDefault$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("recordDefault");

  public static passmode.InnerRecord recordDefault(java.lang.foreign.MemorySegment memorySegment) {
    return passmode.InnerRecordFM.fromMemorySegment$F(memorySegment.asSlice(
        MemoryLayout$F.byteOffset(recordDefault$PathElement$F),
        MemoryLayout$F.select(recordDefault$PathElement$F).byteSize()));
  }

  public static void recordDefault(java.lang.foreign.MemorySegment memorySegment, passmode.InnerRecord value) {
    var memoryLayout =
        MemoryLayout$F.select(recordDefault$PathElement$F);
    var slice = memorySegment.asSlice(
        MemoryLayout$F.byteOffset(recordDefault$PathElement$F),
        memoryLayout.byteSize());
    passmode.InnerRecordFM.toMemorySegment$F(
        value, slice);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      interfaceDefault$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("interfaceDefault");

  public static final java.lang.invoke.VarHandle interfaceDefault$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(interfaceDefault$PathElement$F), 1, 0L);

  public static passmode.InnerInterface interfaceDefault(java.lang.foreign.MemorySegment memorySegment) {
    return passmode.InnerInterfaceFM.reinterpret$F((java.lang.foreign.MemorySegment) interfaceDefault$VarHandle$F.get(memorySegment));
  }

  public static void interfaceDefault(java.lang.foreign.MemorySegment memorySegment, passmode.InnerInterface value) {
    interfaceDefault$VarHandle$F.set(memorySegment, ((passmode.InnerInterfaceFM) value).MemorySegment$F);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      recordTypeUseAddress$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("recordTypeUseAddress");

  public static final java.lang.invoke.VarHandle recordTypeUseAddress$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(recordTypeUseAddress$PathElement$F), 1, 0L);

  public static passmode.InnerRecord recordTypeUseAddress(java.lang.foreign.MemorySegment memorySegment) {
    return passmode.InnerRecordFM.reinterpret$F((java.lang.foreign.MemorySegment) recordTypeUseAddress$VarHandle$F.get(memorySegment));
  }

  public static void recordTypeUseAddress(
      java.lang.foreign.MemorySegment memorySegment, java.lang.foreign.SegmentAllocator allocator, passmode.InnerRecord value) {
    recordTypeUseAddress$VarHandle$F.set(memorySegment,
        passmode.InnerRecordFM.toMemorySegment$F(allocator, value));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      interfaceTypeUseValue$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("interfaceTypeUseValue");

  public static passmode.InnerInterface interfaceTypeUseValue(java.lang.foreign.MemorySegment memorySegment) {
    return new passmode.InnerInterfaceFM(memorySegment.asSlice(
        MemoryLayout$F.byteOffset(interfaceTypeUseValue$PathElement$F),
        MemoryLayout$F.select(interfaceTypeUseValue$PathElement$F).byteSize()));
  }

  public static void interfaceTypeUseValue(java.lang.foreign.MemorySegment memorySegment, passmode.InnerInterface value) {
    var memoryLayout =
        MemoryLayout$F.select(interfaceTypeUseValue$PathElement$F);
    var slice = memorySegment.asSlice(
        MemoryLayout$F.byteOffset(interfaceTypeUseValue$PathElement$F),
        memoryLayout.byteSize());
    java.lang.foreign.MemorySegment.copy(
        ((passmode.InnerInterfaceFM) value).MemorySegment$F, 0,
        slice, 0, memoryLayout.byteSize());
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      fieldOverridesTypeAddress$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("fieldOverridesTypeAddress");

  public static passmode.TypeAddressRecord fieldOverridesTypeAddress(java.lang.foreign.MemorySegment memorySegment) {
    return passmode.TypeAddressRecordFM.fromMemorySegment$F(memorySegment.asSlice(
        MemoryLayout$F.byteOffset(fieldOverridesTypeAddress$PathElement$F),
        MemoryLayout$F.select(fieldOverridesTypeAddress$PathElement$F).byteSize()));
  }

  public static void fieldOverridesTypeAddress(java.lang.foreign.MemorySegment memorySegment, passmode.TypeAddressRecord value) {
    var memoryLayout =
        MemoryLayout$F.select(fieldOverridesTypeAddress$PathElement$F);
    var slice = memorySegment.asSlice(
        MemoryLayout$F.byteOffset(fieldOverridesTypeAddress$PathElement$F),
        memoryLayout.byteSize());
    passmode.TypeAddressRecordFM.toMemorySegment$F(
        value, slice);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      fieldOverridesTypeValue$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("fieldOverridesTypeValue");

  public static final java.lang.invoke.VarHandle fieldOverridesTypeValue$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(fieldOverridesTypeValue$PathElement$F), 1, 0L);

  public static passmode.TypeValueInterface fieldOverridesTypeValue(java.lang.foreign.MemorySegment memorySegment) {
    return passmode.TypeValueInterfaceFM.reinterpret$F((java.lang.foreign.MemorySegment) fieldOverridesTypeValue$VarHandle$F.get(memorySegment));
  }

  public static void fieldOverridesTypeValue(java.lang.foreign.MemorySegment memorySegment, passmode.TypeValueInterface value) {
    fieldOverridesTypeValue$VarHandle$F.set(memorySegment, ((passmode.TypeValueInterfaceFM) value).MemorySegment$F);
  }
}

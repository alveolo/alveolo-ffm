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

  public static FieldModes reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return fromMemorySegment$F(memorySegment$f.reinterpret(MemoryLayout$F.byteSize()));
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

  public static FieldModes at$F(
      java.lang.foreign.MemorySegment array$f, long index$f) {
    return fromMemorySegment$F(elementAt$F(array$f, index$f));
  }

  public static void toMemorySegment$F(
      FieldModes source$f,
      java.lang.foreign.MemorySegment memorySegment$f,
      java.lang.foreign.SegmentAllocator allocator$f) {
    recordDefault(memorySegment$f, source$f.recordDefault());
    interfaceDefault(memorySegment$f, source$f.interfaceDefault());
    recordTypeUseAddress(memorySegment$f, allocator$f, source$f.recordTypeUseAddress());
    interfaceTypeUseValue(memorySegment$f, source$f.interfaceTypeUseValue());
    fieldOverridesTypeAddress(memorySegment$f, source$f.fieldOverridesTypeAddress());
    fieldOverridesTypeValue(memorySegment$f, source$f.fieldOverridesTypeValue());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment$F(
      java.lang.foreign.SegmentAllocator allocator$f,
      FieldModes source$f) {
    var memorySegment$f = allocate$F(allocator$f);
    toMemorySegment$F(source$f, memorySegment$f, allocator$f);
    return memorySegment$f;
  }

  public static FieldModes fromMemorySegment$F(
      java.lang.foreign.MemorySegment memorySegment$f) {
    return new FieldModes(
        recordDefault(memorySegment$f),
        interfaceDefault(memorySegment$f),
        recordTypeUseAddress(memorySegment$f),
        interfaceTypeUseValue(memorySegment$f),
        fieldOverridesTypeAddress(memorySegment$f),
        fieldOverridesTypeValue(memorySegment$f));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      recordDefault$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("recordDefault");

  public static passmode.InnerRecord recordDefault(java.lang.foreign.MemorySegment memorySegment$f) {
    return passmode.InnerRecordFM.fromMemorySegment$F(memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(recordDefault$PathElement$F),
        MemoryLayout$F.select(recordDefault$PathElement$F).byteSize()));
  }

  public static void recordDefault(java.lang.foreign.MemorySegment memorySegment$f, passmode.InnerRecord value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(recordDefault$PathElement$F);
    var slice$f = memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(recordDefault$PathElement$F),
        memoryLayout$f.byteSize());
    passmode.InnerRecordFM.toMemorySegment$F(
        value$f, slice$f);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      interfaceDefault$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("interfaceDefault");

  public static final java.lang.invoke.VarHandle interfaceDefault$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(interfaceDefault$PathElement$F), 1, 0L);

  public static passmode.InnerInterface interfaceDefault(java.lang.foreign.MemorySegment memorySegment$f) {
    return passmode.InnerInterfaceFM.reinterpret$F((java.lang.foreign.MemorySegment) interfaceDefault$VarHandle$F.get(memorySegment$f));
  }

  public static void interfaceDefault(java.lang.foreign.MemorySegment memorySegment$f, passmode.InnerInterface value$f) {
    interfaceDefault$VarHandle$F.set(memorySegment$f, ((passmode.InnerInterfaceFM)value$f).MemorySegment$F);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      recordTypeUseAddress$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("recordTypeUseAddress");

  public static final java.lang.invoke.VarHandle recordTypeUseAddress$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(recordTypeUseAddress$PathElement$F), 1, 0L);

  public static passmode.InnerRecord recordTypeUseAddress(java.lang.foreign.MemorySegment memorySegment$f) {
    return passmode.InnerRecordFM.reinterpret$F((java.lang.foreign.MemorySegment) recordTypeUseAddress$VarHandle$F.get(memorySegment$f));
  }

  public static void recordTypeUseAddress(
      java.lang.foreign.MemorySegment memorySegment$f, java.lang.foreign.SegmentAllocator allocator$f, passmode.InnerRecord value$f) {
    recordTypeUseAddress$VarHandle$F.set(memorySegment$f,
        passmode.InnerRecordFM.toMemorySegment$F(allocator$f, value$f));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      interfaceTypeUseValue$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("interfaceTypeUseValue");

  public static passmode.InnerInterface interfaceTypeUseValue(java.lang.foreign.MemorySegment memorySegment$f) {
    return new passmode.InnerInterfaceFM(memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(interfaceTypeUseValue$PathElement$F),
        MemoryLayout$F.select(interfaceTypeUseValue$PathElement$F).byteSize()));
  }

  public static void interfaceTypeUseValue(java.lang.foreign.MemorySegment memorySegment$f, passmode.InnerInterface value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(interfaceTypeUseValue$PathElement$F);
    var slice$f = memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(interfaceTypeUseValue$PathElement$F),
        memoryLayout$f.byteSize());
    java.lang.foreign.MemorySegment.copy(
        ((passmode.InnerInterfaceFM)value$f).MemorySegment$F, 0,
        slice$f, 0, memoryLayout$f.byteSize());
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      fieldOverridesTypeAddress$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("fieldOverridesTypeAddress");

  public static passmode.TypeAddressRecord fieldOverridesTypeAddress(java.lang.foreign.MemorySegment memorySegment$f) {
    return passmode.TypeAddressRecordFM.fromMemorySegment$F(memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(fieldOverridesTypeAddress$PathElement$F),
        MemoryLayout$F.select(fieldOverridesTypeAddress$PathElement$F).byteSize()));
  }

  public static void fieldOverridesTypeAddress(java.lang.foreign.MemorySegment memorySegment$f, passmode.TypeAddressRecord value$f) {
    var memoryLayout$f =
        MemoryLayout$F.select(fieldOverridesTypeAddress$PathElement$F);
    var slice$f = memorySegment$f.asSlice(
        MemoryLayout$F.byteOffset(fieldOverridesTypeAddress$PathElement$F),
        memoryLayout$f.byteSize());
    passmode.TypeAddressRecordFM.toMemorySegment$F(
        value$f, slice$f);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement
      fieldOverridesTypeValue$PathElement$F = java.lang.foreign.MemoryLayout.PathElement
          .groupElement("fieldOverridesTypeValue");

  public static final java.lang.invoke.VarHandle fieldOverridesTypeValue$VarHandle$F =
      java.lang.invoke.MethodHandles.insertCoordinates(
          MemoryLayout$F.varHandle(fieldOverridesTypeValue$PathElement$F), 1, 0L);

  public static passmode.TypeValueInterface fieldOverridesTypeValue(java.lang.foreign.MemorySegment memorySegment$f) {
    return passmode.TypeValueInterfaceFM.reinterpret$F((java.lang.foreign.MemorySegment) fieldOverridesTypeValue$VarHandle$F.get(memorySegment$f));
  }

  public static void fieldOverridesTypeValue(java.lang.foreign.MemorySegment memorySegment$f, passmode.TypeValueInterface value$f) {
    fieldOverridesTypeValue$VarHandle$F.set(memorySegment$f, ((passmode.TypeValueInterfaceFM)value$f).MemorySegment$F);
  }
}

package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class FieldModesFM {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        pkg.InnerRecordFM.FM$LAYOUT.withName("recordDefault"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("interfaceDefault"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("recordTypeUseAddress"),
        pkg.InnerInterfaceFM.FM$LAYOUT.withName("interfaceTypeUseValue"),
        pkg.TypeAddressRecordFM.FM$LAYOUT.withName("fieldOverridesTypeAddress"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("fieldOverridesTypeValue"),
      }));

  public static java.lang.foreign.MemorySegment allocate(java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static FieldModes reinterpret(java.lang.foreign.MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret(
      java.lang.foreign.MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment FM$at(java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static FieldModes at(java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(
      FieldModes from, java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator ff$allocator) {
    recordDefault(ms, from.recordDefault());
    interfaceDefault(ms, from.interfaceDefault());
    recordTypeUseAddress(ms, ff$allocator, from.recordTypeUseAddress());
    interfaceTypeUseValue(ms, from.interfaceTypeUseValue());
    fieldOverridesTypeAddress(ms, from.fieldOverridesTypeAddress());
    fieldOverridesTypeValue(ms, from.fieldOverridesTypeValue());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment(
      java.lang.foreign.SegmentAllocator allocator, FieldModes from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static FieldModes fromMemorySegment(java.lang.foreign.MemorySegment ms) {
    return new FieldModes(
        recordDefault(ms),
        interfaceDefault(ms),
        recordTypeUseAddress(ms),
        interfaceTypeUseValue(ms),
        fieldOverridesTypeAddress(ms),
        fieldOverridesTypeValue(ms));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$recordDefault =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("recordDefault");

  public static pkg.InnerRecord recordDefault(java.lang.foreign.MemorySegment ms) {
    return pkg.InnerRecordFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$recordDefault),
        FM$LAYOUT.select(FM$PE$recordDefault).byteSize()));
  }

  public static void recordDefault(java.lang.foreign.MemorySegment ms, pkg.InnerRecord value) {
    var layout = FM$LAYOUT.select(FM$PE$recordDefault);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$recordDefault), layout.byteSize());
    pkg.InnerRecordFM.toMemorySegment(value, slice);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$interfaceDefault =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("interfaceDefault");

  public static final java.lang.invoke.VarHandle FM$VH$interfaceDefault =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$interfaceDefault), 1, 0L);

  public static pkg.InnerInterface interfaceDefault(java.lang.foreign.MemorySegment ms) {
    return pkg.InnerInterfaceFM.reinterpret((java.lang.foreign.MemorySegment) FM$VH$interfaceDefault.get(ms));
  }

  public static void interfaceDefault(java.lang.foreign.MemorySegment ms, pkg.InnerInterface value) {
    FM$VH$interfaceDefault.set(ms, ((pkg.InnerInterfaceFM) value).ms);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$recordTypeUseAddress =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("recordTypeUseAddress");

  public static final java.lang.invoke.VarHandle FM$VH$recordTypeUseAddress =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$recordTypeUseAddress), 1, 0L);

  public static pkg.InnerRecord recordTypeUseAddress(java.lang.foreign.MemorySegment ms) {
    return pkg.InnerRecordFM.reinterpret((java.lang.foreign.MemorySegment) FM$VH$recordTypeUseAddress.get(ms));
  }

  public static void recordTypeUseAddress(
      java.lang.foreign.MemorySegment ms, java.lang.foreign.SegmentAllocator allocator, pkg.InnerRecord value) {
    FM$VH$recordTypeUseAddress.set(ms,
        pkg.InnerRecordFM.toMemorySegment(allocator, value));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$interfaceTypeUseValue =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("interfaceTypeUseValue");

  public static pkg.InnerInterface interfaceTypeUseValue(java.lang.foreign.MemorySegment ms) {
    return new pkg.InnerInterfaceFM(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$interfaceTypeUseValue),
        FM$LAYOUT.select(FM$PE$interfaceTypeUseValue).byteSize()));
  }

  public static void interfaceTypeUseValue(java.lang.foreign.MemorySegment ms, pkg.InnerInterface value) {
    var layout = FM$LAYOUT.select(FM$PE$interfaceTypeUseValue);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$interfaceTypeUseValue), layout.byteSize());
    java.lang.foreign.MemorySegment.copy(((pkg.InnerInterfaceFM)value).ms, 0,
        slice, 0, layout.byteSize());
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$fieldOverridesTypeAddress =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("fieldOverridesTypeAddress");

  public static pkg.TypeAddressRecord fieldOverridesTypeAddress(java.lang.foreign.MemorySegment ms) {
    return pkg.TypeAddressRecordFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$fieldOverridesTypeAddress),
        FM$LAYOUT.select(FM$PE$fieldOverridesTypeAddress).byteSize()));
  }

  public static void fieldOverridesTypeAddress(java.lang.foreign.MemorySegment ms, pkg.TypeAddressRecord value) {
    var layout = FM$LAYOUT.select(FM$PE$fieldOverridesTypeAddress);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$fieldOverridesTypeAddress), layout.byteSize());
    pkg.TypeAddressRecordFM.toMemorySegment(value, slice);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$fieldOverridesTypeValue =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("fieldOverridesTypeValue");

  public static final java.lang.invoke.VarHandle FM$VH$fieldOverridesTypeValue =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$fieldOverridesTypeValue), 1, 0L);

  public static pkg.TypeValueInterface fieldOverridesTypeValue(java.lang.foreign.MemorySegment ms) {
    return pkg.TypeValueInterfaceFM.reinterpret((java.lang.foreign.MemorySegment) FM$VH$fieldOverridesTypeValue.get(ms));
  }

  public static void fieldOverridesTypeValue(java.lang.foreign.MemorySegment ms, pkg.TypeValueInterface value) {
    FM$VH$fieldOverridesTypeValue.set(ms, ((pkg.TypeValueInterfaceFM) value).ms);
  }
}

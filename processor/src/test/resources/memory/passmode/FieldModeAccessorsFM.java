package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class FieldModeAccessorsFM implements FieldModeAccessors {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        pkg.InnerRecordFM.FM$LAYOUT.withName("recordDefault"),
        java.lang.foreign.ValueLayout.ADDRESS.withName("interfaceDefault"),
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

  public static FieldModeAccessorsFM reinterpret(java.lang.foreign.MemorySegment ms) {
    return new FieldModeAccessorsFM(ms.reinterpret(FM$LAYOUT.byteSize()));
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

  public static FieldModeAccessorsFM at(java.lang.foreign.MemorySegment array, long index) {
    return new FieldModeAccessorsFM(FM$at(array, index));
  }

  public final java.lang.foreign.MemorySegment ms;

  public FieldModeAccessorsFM(java.lang.foreign.SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public FieldModeAccessorsFM(java.lang.foreign.MemorySegment ms) {
    this.ms = ms;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$recordDefault =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("recordDefault");

  public pkg.InnerRecord recordDefault() {
    return pkg.InnerRecordFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$recordDefault),
        FM$LAYOUT.select(FM$PE$recordDefault).byteSize()));
  }

  public FieldModeAccessorsFM recordDefault(pkg.InnerRecord value) {
    var layout = FM$LAYOUT.select(FM$PE$recordDefault);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$recordDefault), layout.byteSize());
    pkg.InnerRecordFM.toMemorySegment(value, slice);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$interfaceDefault =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("interfaceDefault");

  public static final java.lang.invoke.VarHandle FM$VH$interfaceDefault =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$interfaceDefault), 1, 0L);

  public pkg.InnerInterface interfaceDefault() {
    return pkg.InnerInterfaceFM.reinterpret((java.lang.foreign.MemorySegment) FM$VH$interfaceDefault.get(ms));
  }

  public FieldModeAccessorsFM interfaceDefault(pkg.InnerInterface value) {
    FM$VH$interfaceDefault.set(ms, ((pkg.InnerInterfaceFM) value).ms);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$interfaceTypeUseValue =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("interfaceTypeUseValue");

  public pkg.InnerInterface interfaceTypeUseValue() {
    return new pkg.InnerInterfaceFM(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$interfaceTypeUseValue),
        FM$LAYOUT.select(FM$PE$interfaceTypeUseValue).byteSize()));
  }

  public FieldModeAccessorsFM interfaceTypeUseValue(pkg.InnerInterface value) {
    var layout = FM$LAYOUT.select(FM$PE$interfaceTypeUseValue);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$interfaceTypeUseValue), layout.byteSize());
    java.lang.foreign.MemorySegment.copy(((pkg.InnerInterfaceFM)value).ms, 0,
        slice, 0, layout.byteSize());
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$fieldOverridesTypeAddress =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("fieldOverridesTypeAddress");

  public pkg.TypeAddressRecord fieldOverridesTypeAddress() {
    return pkg.TypeAddressRecordFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$fieldOverridesTypeAddress),
        FM$LAYOUT.select(FM$PE$fieldOverridesTypeAddress).byteSize()));
  }

  public FieldModeAccessorsFM fieldOverridesTypeAddress(pkg.TypeAddressRecord value) {
    var layout = FM$LAYOUT.select(FM$PE$fieldOverridesTypeAddress);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$fieldOverridesTypeAddress), layout.byteSize());
    pkg.TypeAddressRecordFM.toMemorySegment(value, slice);
    return this;
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$fieldOverridesTypeValue =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("fieldOverridesTypeValue");

  public static final java.lang.invoke.VarHandle FM$VH$fieldOverridesTypeValue =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$fieldOverridesTypeValue), 1, 0L);

  public pkg.TypeValueInterface fieldOverridesTypeValue() {
    return pkg.TypeValueInterfaceFM.reinterpret((java.lang.foreign.MemorySegment) FM$VH$fieldOverridesTypeValue.get(ms));
  }

  public FieldModeAccessorsFM fieldOverridesTypeValue(pkg.TypeValueInterface value) {
    FM$VH$fieldOverridesTypeValue.set(ms, ((pkg.TypeValueInterfaceFM) value).ms);
    return this;
  }
}

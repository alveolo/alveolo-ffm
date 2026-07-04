package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class FieldModeAccessorsFM implements FieldModeAccessors {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new MemoryLayout [] {
        pkg.InnerRecordFM.FM$LAYOUT.withName("recordDefault"),
        ValueLayout.ADDRESS.withName("interfaceDefault"),
        pkg.InnerInterfaceFM.FM$LAYOUT.withName("interfaceTypeUseValue"),
        pkg.TypeAddressRecordFM.FM$LAYOUT.withName("fieldOverridesTypeAddress"),
        ValueLayout.ADDRESS.withName("fieldOverridesTypeValue"),
      }));

  public static final MemoryLayout.PathElement FM$PE$recordDefault =
      MemoryLayout.PathElement.groupElement("recordDefault");

  public static final MemoryLayout.PathElement FM$PE$interfaceDefault =
      MemoryLayout.PathElement.groupElement("interfaceDefault");

  public static final MemoryLayout.PathElement FM$PE$interfaceTypeUseValue =
      MemoryLayout.PathElement.groupElement("interfaceTypeUseValue");

  public static final MemoryLayout.PathElement FM$PE$fieldOverridesTypeAddress =
      MemoryLayout.PathElement.groupElement("fieldOverridesTypeAddress");

  public static final MemoryLayout.PathElement FM$PE$fieldOverridesTypeValue =
      MemoryLayout.PathElement.groupElement("fieldOverridesTypeValue");

  public static final java.lang.invoke.VarHandle FM$VH$interfaceDefault =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$interfaceDefault), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$fieldOverridesTypeValue =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$fieldOverridesTypeValue), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static FieldModeAccessorsFM reinterpret(MemorySegment ms) {
    return new FieldModeAccessorsFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public FieldModeAccessorsFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public FieldModeAccessorsFM(MemorySegment ms) {
    this.ms = ms;
  }

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

  public pkg.InnerInterface interfaceDefault() {
    return pkg.InnerInterfaceFM.reinterpret((MemorySegment) FM$VH$interfaceDefault.get(ms));
  }

  public FieldModeAccessorsFM interfaceDefault(pkg.InnerInterface value) {
    FM$VH$interfaceDefault.set(ms, ((pkg.InnerInterfaceFM)value).ms);
    return this;
  }

  public pkg.InnerInterface interfaceTypeUseValue() {
    return new pkg.InnerInterfaceFM(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$interfaceTypeUseValue),
        FM$LAYOUT.select(FM$PE$interfaceTypeUseValue).byteSize()));
  }

  public FieldModeAccessorsFM interfaceTypeUseValue(pkg.InnerInterface value) {
    var layout = FM$LAYOUT.select(FM$PE$interfaceTypeUseValue);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$interfaceTypeUseValue), layout.byteSize());
    MemorySegment.copy(((pkg.InnerInterfaceFM)value).ms, 0,
        slice, 0, layout.byteSize());
    return this;
  }

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

  public pkg.TypeValueInterface fieldOverridesTypeValue() {
    return pkg.TypeValueInterfaceFM.reinterpret((MemorySegment) FM$VH$fieldOverridesTypeValue.get(ms));
  }

  public FieldModeAccessorsFM fieldOverridesTypeValue(pkg.TypeValueInterface value) {
    FM$VH$fieldOverridesTypeValue.set(ms, ((pkg.TypeValueInterfaceFM)value).ms);
    return this;
  }
}

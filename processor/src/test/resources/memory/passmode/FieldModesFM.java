package pkg;

import java.lang.foreign.*;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class FieldModesFM {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
        pkg.InnerRecordFM.FM$LAYOUT.withName("recordDefault"),
        ValueLayout.ADDRESS.withName("interfaceDefault"),
        ValueLayout.ADDRESS.withName("recordTypeUseAddress"),
        pkg.InnerInterfaceFM.FM$LAYOUT.withName("interfaceTypeUseValue"),
        pkg.TypeAddressRecordFM.FM$LAYOUT.withName("fieldOverridesTypeAddress"),
        ValueLayout.ADDRESS.withName("fieldOverridesTypeValue"),
      }));

  public static final MemoryLayout.PathElement FM$PE$recordDefault =
      MemoryLayout.PathElement.groupElement("recordDefault");

  public static final MemoryLayout.PathElement FM$PE$interfaceDefault =
      MemoryLayout.PathElement.groupElement("interfaceDefault");

  public static final MemoryLayout.PathElement FM$PE$recordTypeUseAddress =
      MemoryLayout.PathElement.groupElement("recordTypeUseAddress");

  public static final MemoryLayout.PathElement FM$PE$interfaceTypeUseValue =
      MemoryLayout.PathElement.groupElement("interfaceTypeUseValue");

  public static final MemoryLayout.PathElement FM$PE$fieldOverridesTypeAddress =
      MemoryLayout.PathElement.groupElement("fieldOverridesTypeAddress");

  public static final MemoryLayout.PathElement FM$PE$fieldOverridesTypeValue =
      MemoryLayout.PathElement.groupElement("fieldOverridesTypeValue");

  public static final java.lang.invoke.VarHandle FM$VH$interfaceDefault =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$interfaceDefault), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$recordTypeUseAddress =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$recordTypeUseAddress), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$fieldOverridesTypeValue =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$fieldOverridesTypeValue), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static FieldModes reinterpret(MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static void toMemorySegment(FieldModes from, MemorySegment ms) {
    try (var ff$arena = Arena.ofConfined()) {
  toMemorySegment(from, ms, ff$arena);
}
  }

  private static void toMemorySegment(
      FieldModes from, MemorySegment ms, SegmentAllocator ff$allocator) {
    recordDefault(ms, from.recordDefault());
    interfaceDefault(ms, from.interfaceDefault());
    recordTypeUseAddress(ms, ff$allocator, from.recordTypeUseAddress());
    interfaceTypeUseValue(ms, from.interfaceTypeUseValue());
    fieldOverridesTypeAddress(ms, from.fieldOverridesTypeAddress());
    fieldOverridesTypeValue(ms, from.fieldOverridesTypeValue());
  }

  public static MemorySegment toMemorySegment(
      SegmentAllocator allocator, FieldModes from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms, allocator);
    return ms;
  }

  public static FieldModes fromMemorySegment(MemorySegment ms) {
    return new FieldModes(
        recordDefault(ms),
        interfaceDefault(ms),
        recordTypeUseAddress(ms),
        interfaceTypeUseValue(ms),
        fieldOverridesTypeAddress(ms),
        fieldOverridesTypeValue(ms));
  }

  public static pkg.InnerRecord recordDefault(MemorySegment ms) {
    return pkg.InnerRecordFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$recordDefault),
        FM$LAYOUT.select(FM$PE$recordDefault).byteSize()));
  }

  public static void recordDefault(MemorySegment ms, pkg.InnerRecord value) {
    var layout = FM$LAYOUT.select(FM$PE$recordDefault);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$recordDefault), layout.byteSize());
    pkg.InnerRecordFM.toMemorySegment(value, slice);
  }

  public static pkg.InnerInterface interfaceDefault(MemorySegment ms) {
    return pkg.InnerInterfaceFM.reinterpret((MemorySegment) FM$VH$interfaceDefault.get(ms));
  }

  public static void interfaceDefault(MemorySegment ms, pkg.InnerInterface value) {
    FM$VH$interfaceDefault.set(ms, ((pkg.InnerInterfaceFM)value).ms);
  }

  public static pkg.InnerRecord recordTypeUseAddress(MemorySegment ms) {
    return pkg.InnerRecordFM.reinterpret((MemorySegment) FM$VH$recordTypeUseAddress.get(ms));
  }

  public static void recordTypeUseAddress(MemorySegment ms, pkg.InnerRecord value) {
    FM$VH$recordTypeUseAddress.set(ms, pkg.InnerRecordFM.toMemorySegment(Arena.ofAuto(), value));
  }

  public static void recordTypeUseAddress(
      MemorySegment ms, SegmentAllocator allocator, pkg.InnerRecord value) {
    FM$VH$recordTypeUseAddress.set(ms,
        pkg.InnerRecordFM.toMemorySegment(allocator, value));
  }

  public static pkg.InnerInterface interfaceTypeUseValue(MemorySegment ms) {
    return new pkg.InnerInterfaceFM(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$interfaceTypeUseValue),
        FM$LAYOUT.select(FM$PE$interfaceTypeUseValue).byteSize()));
  }

  public static void interfaceTypeUseValue(MemorySegment ms, pkg.InnerInterface value) {
    var layout = FM$LAYOUT.select(FM$PE$interfaceTypeUseValue);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$interfaceTypeUseValue), layout.byteSize());
    MemorySegment.copy(((pkg.InnerInterfaceFM)value).ms, 0,
        slice, 0, layout.byteSize());
  }

  public static pkg.TypeAddressRecord fieldOverridesTypeAddress(MemorySegment ms) {
    return pkg.TypeAddressRecordFM.fromMemorySegment(ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$fieldOverridesTypeAddress),
        FM$LAYOUT.select(FM$PE$fieldOverridesTypeAddress).byteSize()));
  }

  public static void fieldOverridesTypeAddress(MemorySegment ms, pkg.TypeAddressRecord value) {
    var layout = FM$LAYOUT.select(FM$PE$fieldOverridesTypeAddress);
    var slice = ms.asSlice(
        FM$LAYOUT.byteOffset(FM$PE$fieldOverridesTypeAddress), layout.byteSize());
    pkg.TypeAddressRecordFM.toMemorySegment(value, slice);
  }

  public static pkg.TypeValueInterface fieldOverridesTypeValue(MemorySegment ms) {
    return pkg.TypeValueInterfaceFM.reinterpret((MemorySegment) FM$VH$fieldOverridesTypeValue.get(ms));
  }

  public static void fieldOverridesTypeValue(MemorySegment ms, pkg.TypeValueInterface value) {
    FM$VH$fieldOverridesTypeValue.set(ms, ((pkg.TypeValueInterfaceFM)value).ms);
  }
}

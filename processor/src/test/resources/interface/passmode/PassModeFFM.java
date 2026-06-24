package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated("org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class PassModeFFM implements PassMode {
  public static final PassModeFFM INSTANCE = new PassModeFFM();

  private PassModeFFM() {}

  static {
  }

  private static final Linker FF$LINKER = Linker.nativeLinker();

  private static final SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();

  private static final MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("recordDefault").get(),
      FunctionDescriptor.of(
          pkg.RecordDefaultFM.FM$LAYOUT,
          pkg.RecordDefaultFM.FM$LAYOUT));

  public pkg.RecordDefault recordDefault(
      pkg.RecordDefault value) {
    try (var ff$arena = Arena.ofConfined()) {
      return pkg.RecordDefaultFM.fromMemorySegment((MemorySegment) FF$MH$0.invokeExact(
          (SegmentAllocator) ff$arena,
          pkg.RecordDefaultFM.toMemorySegment(ff$arena, value)));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$1 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("recordAddress").get(),
      FunctionDescriptor.of(
          ValueLayout.ADDRESS,
          ValueLayout.ADDRESS));

  public pkg.@org.alveolo.ffm.Address RecordDefault recordAddress(
      pkg.RecordDefault ref) {
    try (var ff$arena = Arena.ofConfined()) {
      return pkg.RecordDefaultFM.fromMemorySegment(((MemorySegment) FF$MH$1.invokeExact(
          pkg.RecordDefaultFM.toMemorySegment(ff$arena, ref))).reinterpret(pkg.RecordDefaultFM.FM$LAYOUT.byteSize()));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$2 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("interfaceDefault").get(),
      FunctionDescriptor.of(
          ValueLayout.ADDRESS,
          ValueLayout.ADDRESS));

  public pkg.InterfaceDefault interfaceDefault(
      pkg.InterfaceDefault ref) {
    try {
      return new pkg.InterfaceDefaultFM(((MemorySegment) FF$MH$2.invokeExact(
          ((pkg.InterfaceDefaultFM)ref).ms)).reinterpret(pkg.InterfaceDefaultFM.FM$LAYOUT.byteSize()));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$3 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("interfaceValue").get(),
      FunctionDescriptor.of(
          pkg.InterfaceDefaultFM.FM$LAYOUT,
          pkg.InterfaceDefaultFM.FM$LAYOUT));

  public pkg.@org.alveolo.ffm.Value InterfaceDefault interfaceValue(
      java.lang.foreign.SegmentAllocator allocator,
      pkg.InterfaceDefault value) {
    try {
      return new pkg.InterfaceDefaultFM((MemorySegment) FF$MH$3.invokeExact(
          allocator,
          ((pkg.InterfaceDefaultFM)value).ms));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$4 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("valueTypeUseOverridesAddressType").get(),
      FunctionDescriptor.of(
          pkg.RecordAddressFM.FM$LAYOUT,
          pkg.RecordAddressFM.FM$LAYOUT));

  public pkg.@org.alveolo.ffm.Value RecordAddress valueTypeUseOverridesAddressType(
      pkg.RecordAddress value) {
    try (var ff$arena = Arena.ofConfined()) {
      return pkg.RecordAddressFM.fromMemorySegment((MemorySegment) FF$MH$4.invokeExact(
          (SegmentAllocator) ff$arena,
          pkg.RecordAddressFM.toMemorySegment(ff$arena, value)));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$5 = FF$LINKER.downcallHandle(
      FF$LOOKUP.find("addressTypeUseOverridesValueType").get(),
      FunctionDescriptor.of(
          ValueLayout.ADDRESS,
          ValueLayout.ADDRESS));

  public pkg.@org.alveolo.ffm.Address InterfaceValue addressTypeUseOverridesValueType(
      pkg.InterfaceValue ref) {
    try {
      return new pkg.InterfaceValueFM(((MemorySegment) FF$MH$5.invokeExact(
          ((pkg.InterfaceValueFM)ref).ms)).reinterpret(pkg.InterfaceValueFM.FM$LAYOUT.byteSize()));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

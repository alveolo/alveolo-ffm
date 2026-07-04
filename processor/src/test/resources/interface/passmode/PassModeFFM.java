package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class PassModeFFM implements PassMode {
  public static final PassModeFFM INSTANCE = new PassModeFFM();

  private PassModeFFM() {}

  public static final Linker FF$LINKER = Linker.nativeLinker();

  public static final SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();

  private static final MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("recordDefault"),
      FunctionDescriptor.of(
          pkg.RecordDefaultFM.FM$LAYOUT,
          pkg.RecordDefaultFM.FM$LAYOUT));

  public pkg.RecordDefault recordDefault(pkg.RecordDefault value) {
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
      FF$LOOKUP.findOrThrow("recordAddress"),
      FunctionDescriptor.of(
          ValueLayout.ADDRESS,
          ValueLayout.ADDRESS));

  public pkg.@org.alveolo.ffm.Address RecordDefault recordAddress(
      pkg.RecordDefault ref) {
    try (var ff$arena = Arena.ofConfined()) {
      return pkg.RecordDefaultFM.reinterpret((MemorySegment) FF$MH$1.invokeExact(
          pkg.RecordDefaultFM.toMemorySegment(ff$arena, ref)));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$2 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("interfaceDefault"),
      FunctionDescriptor.of(
          ValueLayout.ADDRESS,
          ValueLayout.ADDRESS));

  public pkg.InterfaceDefault interfaceDefault(
      pkg.InterfaceDefault ref) {
    try {
      return pkg.InterfaceDefaultFM.reinterpret((MemorySegment) FF$MH$2.invokeExact(
          ((pkg.InterfaceDefaultFM)ref).ms));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$3 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("interfaceValue"),
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
      FF$LOOKUP.findOrThrow("valueTypeUseOverridesAddressType"),
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
      FF$LOOKUP.findOrThrow("addressTypeUseOverridesValueType"),
      FunctionDescriptor.of(
          ValueLayout.ADDRESS,
          ValueLayout.ADDRESS));

  public pkg.@org.alveolo.ffm.Address InterfaceValue addressTypeUseOverridesValueType(
      pkg.InterfaceValue ref) {
    try {
      return pkg.InterfaceValueFM.reinterpret((MemorySegment) FF$MH$5.invokeExact(
          ((pkg.InterfaceValueFM)ref).ms));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$6 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("primitiveAddress"),
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.ADDRESS));

  public int primitiveAddress(
      int value) {
    try (var ff$arena = Arena.ofConfined()) {
      var ff$ms$value = ff$arena.allocate(ValueLayout.JAVA_INT);
      ff$ms$value.set(ValueLayout.JAVA_INT, 0L, value);
      return (int) FF$MH$6.invokeExact(
          ff$ms$value);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$7 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("primitiveAddressReturn"),
      FunctionDescriptor.of(
          ValueLayout.ADDRESS));

  public int primitiveAddressReturn() {
    try {
      var ff$address$r = (MemorySegment) FF$MH$7.invokeExact();
      return ff$address$r.reinterpret(ValueLayout.JAVA_INT.byteSize())
          .get(ValueLayout.JAVA_INT, 0L);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

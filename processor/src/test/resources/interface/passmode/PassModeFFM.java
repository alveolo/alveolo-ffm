package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class PassModeFFM implements PassMode {
  public static final PassModeFFM INSTANCE = new PassModeFFM();

  private PassModeFFM() {}

  public static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();

  private static final java.lang.invoke.MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("recordDefault"),
      java.lang.foreign.FunctionDescriptor.of(
          pkg.RecordDefaultFM.FM$LAYOUT,
          pkg.RecordDefaultFM.FM$LAYOUT));

  public pkg.RecordDefault recordDefault(pkg.RecordDefault value) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      return pkg.RecordDefaultFM.fromMemorySegment((java.lang.foreign.MemorySegment) FF$MH$0.invokeExact(
          (java.lang.foreign.SegmentAllocator) ff$arena,
          pkg.RecordDefaultFM.toMemorySegment(ff$arena, value)));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$1 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("recordAddress"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS));

  public pkg.@org.alveolo.ffm.Address RecordDefault recordAddress(
      pkg.RecordDefault ref) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      return pkg.RecordDefaultFM.reinterpret((java.lang.foreign.MemorySegment) FF$MH$1.invokeExact(
          pkg.RecordDefaultFM.toMemorySegment(ff$arena, ref)));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$2 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("interfaceDefault"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS));

  public pkg.InterfaceDefault interfaceDefault(
      pkg.InterfaceDefault ref) {
    try {
      return pkg.InterfaceDefaultFM.reinterpret((java.lang.foreign.MemorySegment) FF$MH$2.invokeExact(
          ((pkg.InterfaceDefaultFM)ref).ms));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$3 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("interfaceValue"),
      java.lang.foreign.FunctionDescriptor.of(
          pkg.InterfaceDefaultFM.FM$LAYOUT,
          pkg.InterfaceDefaultFM.FM$LAYOUT));

  public pkg.@org.alveolo.ffm.Value InterfaceDefault interfaceValue(
      java.lang.foreign.SegmentAllocator allocator,
      pkg.InterfaceDefault value) {
    try {
      return new pkg.InterfaceDefaultFM((java.lang.foreign.MemorySegment) FF$MH$3.invokeExact(
          allocator,
          ((pkg.InterfaceDefaultFM)value).ms));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$4 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("valueTypeUseOverridesAddressType"),
      java.lang.foreign.FunctionDescriptor.of(
          pkg.RecordAddressFM.FM$LAYOUT,
          pkg.RecordAddressFM.FM$LAYOUT));

  public pkg.@org.alveolo.ffm.Value RecordAddress valueTypeUseOverridesAddressType(
      pkg.RecordAddress value) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      return pkg.RecordAddressFM.fromMemorySegment((java.lang.foreign.MemorySegment) FF$MH$4.invokeExact(
          (java.lang.foreign.SegmentAllocator) ff$arena,
          pkg.RecordAddressFM.toMemorySegment(ff$arena, value)));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$5 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("addressTypeUseOverridesValueType"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS));

  public pkg.@org.alveolo.ffm.Address InterfaceValue addressTypeUseOverridesValueType(
      pkg.InterfaceValue ref) {
    try {
      return pkg.InterfaceValueFM.reinterpret((java.lang.foreign.MemorySegment) FF$MH$5.invokeExact(
          ((pkg.InterfaceValueFM)ref).ms));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$6 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("primitiveAddress"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int primitiveAddress(
      int value) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$ms$value = ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT);
      ff$ms$value.set(java.lang.foreign.ValueLayout.JAVA_INT, 0L, value);
      return (int) FF$MH$6.invokeExact(
          ff$ms$value);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$7 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("primitiveAddressReturn"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS));

  public int primitiveAddressReturn() {
    try {
      var ff$address$r = (java.lang.foreign.MemorySegment) FF$MH$7.invokeExact();
      return ff$address$r.reinterpret(java.lang.foreign.ValueLayout.JAVA_INT.byteSize())
          .get(java.lang.foreign.ValueLayout.JAVA_INT, 0L);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

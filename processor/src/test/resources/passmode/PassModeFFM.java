package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class PassModeFFM implements PassMode {
  public static final PassModeFFM INSTANCE$F = new PassModeFFM();

  private PassModeFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("originalInterfaces"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          passmode.ValueStructFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int originalInterfaces(
      passmode.DefaultStruct defaultValue,
      passmode.ValueStruct value,
      passmode.AddressStruct address) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          ((passmode.DefaultStructFM) defaultValue).MemorySegment$F,
          ((passmode.ValueStructFM) value).MemorySegment$F,
          ((passmode.AddressStructFM) address).MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("generatedInterfaces"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          passmode.ValueStructFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int generatedInterfaces(
      passmode.DefaultStructFM defaultValue,
      passmode.ValueStructFM value,
      passmode.AddressStructFM address) {
    try {
      return (int) MethodHandle$1$F.invokeExact(
          defaultValue.MemorySegment$F,
          value.MemorySegment$F,
          address.MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("originalCircular"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          passmode.CircularValue.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int originalCircular(
      passmode.CircularDefaultSpec defaultValue,
      passmode.CircularValueSpec value,
      passmode.CircularAddressSpec address) {
    try {
      return (int) MethodHandle$2$F.invokeExact(
          ((passmode.CircularDefault) defaultValue).MemorySegment$F,
          ((passmode.CircularValue) value).MemorySegment$F,
          ((passmode.CircularAddress) address).MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$3$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("generatedCircular"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          passmode.CircularValue.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int generatedCircular(
      passmode.CircularDefault defaultValue,
      passmode.CircularValue value,
      passmode.CircularAddress address) {
    try {
      return (int) MethodHandle$3$F.invokeExact(
          defaultValue.MemorySegment$F,
          value.MemorySegment$F,
          address.MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$4$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("originalRecords"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          passmode.DefaultRecordFM.MemoryLayout$F,
          passmode.ValueRecordFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int originalRecords(
      passmode.DefaultRecord defaultValue,
      passmode.ValueRecord value,
      passmode.AddressRecord address) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (int) MethodHandle$4$F.invokeExact(
          passmode.DefaultRecordFM.toMemorySegment$F(arena$f, defaultValue),
          passmode.ValueRecordFM.toMemorySegment$F(arena$f, value),
          passmode.AddressRecordFM.toMemorySegment$F(arena$f, address));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$5$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("originalOverrides"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          passmode.AddressStructFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS,
          passmode.AddressRecordFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int originalOverrides(
      passmode.AddressStruct interfaceValue,
      passmode.ValueStruct interfaceAddress,
      passmode.AddressRecord recordValue,
      passmode.ValueRecord recordAddress) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (int) MethodHandle$5$F.invokeExact(
          ((passmode.AddressStructFM) interfaceValue).MemorySegment$F,
          ((passmode.ValueStructFM) interfaceAddress).MemorySegment$F,
          passmode.AddressRecordFM.toMemorySegment$F(arena$f, recordValue),
          passmode.ValueRecordFM.toMemorySegment$F(arena$f, recordAddress));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$6$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("generatedOverrides"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          passmode.AddressStructFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int generatedOverrides(
      passmode.AddressStructFM interfaceValue,
      passmode.ValueStructFM interfaceAddress) {
    try {
      return (int) MethodHandle$6$F.invokeExact(
          interfaceValue.MemorySegment$F,
          interfaceAddress.MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$7$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("originalRecordReturn"),
      java.lang.foreign.FunctionDescriptor.of(
          passmode.DefaultRecordFM.MemoryLayout$F,
          passmode.DefaultRecordFM.MemoryLayout$F));

  public passmode.DefaultRecord originalRecordReturn(
      passmode.DefaultRecord value) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return passmode.DefaultRecordFM.fromMemorySegment$F((java.lang.foreign.MemorySegment) MethodHandle$7$F.invokeExact(
          (java.lang.foreign.SegmentAllocator) arena$f,
          passmode.DefaultRecordFM.toMemorySegment$F(arena$f, value)));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$8$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("originalRecordAddressReturn"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS));

  public passmode.@org.alveolo.ffm.Address DefaultRecord originalRecordAddressReturn(
      passmode.DefaultRecord value) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return passmode.DefaultRecordFM.reinterpret$F((java.lang.foreign.MemorySegment) MethodHandle$8$F.invokeExact(
          passmode.DefaultRecordFM.toMemorySegment$F(arena$f, value)));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$9$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("originalInterfaceReturn"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS));

  public passmode.DefaultStruct originalInterfaceReturn(
      passmode.DefaultStruct value) {
    try {
      return passmode.DefaultStructFM.reinterpret$F((java.lang.foreign.MemorySegment) MethodHandle$9$F.invokeExact(
          ((passmode.DefaultStructFM) value).MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$10$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("originalInterfaceValueReturn"),
      java.lang.foreign.FunctionDescriptor.of(
          passmode.DefaultStructFM.MemoryLayout$F,
          passmode.DefaultStructFM.MemoryLayout$F));

  public passmode.@org.alveolo.ffm.Value DefaultStruct originalInterfaceValueReturn(
      java.lang.foreign.SegmentAllocator allocator,
      passmode.DefaultStruct value) {
    try {
      return new passmode.DefaultStructFM((java.lang.foreign.MemorySegment) MethodHandle$10$F.invokeExact(
          allocator,
          ((passmode.DefaultStructFM) value).MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$11$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("primitiveAddress"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int primitiveAddress(
      int value) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var value$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT);
      value$MemorySegment$f.set(java.lang.foreign.ValueLayout.JAVA_INT, 0L, value);
      return (int) MethodHandle$11$F.invokeExact(
          value$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$12$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("primitiveAddressReturn"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS));

  public int primitiveAddressReturn(
      ) {
    try {
      var addressResult$f = (java.lang.foreign.MemorySegment) MethodHandle$12$F.invokeExact(
          );
      return addressResult$f.reinterpret(java.lang.foreign.ValueLayout.JAVA_INT.byteSize())
          .get(java.lang.foreign.ValueLayout.JAVA_INT, 0L);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

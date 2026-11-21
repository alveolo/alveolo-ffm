package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class PassModeF implements PassMode {
  public static final PassModeF INSTANCE$F = new PassModeF();

  private PassModeF() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      SymbolLookup$F.find("originalInterfaces")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  passmode.ValueStructF.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int originalInterfaces(
      passmode.DefaultStruct defaultValue,
      passmode.ValueStruct value,
      passmode.AddressStruct address) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: originalInterfaces");
    try {
      return (int) MethodHandle$0$F.invokeExact(
          (java.lang.foreign.MemorySegment) (defaultValue == null ? java.lang.foreign.MemorySegment.NULL : ((passmode.DefaultStructF) defaultValue).MemorySegment$F),
          ((passmode.ValueStructF) value).MemorySegment$F,
          (java.lang.foreign.MemorySegment) (address == null ? java.lang.foreign.MemorySegment.NULL : ((passmode.AddressStructF) address).MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      SymbolLookup$F.find("generatedInterfaces")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  passmode.ValueStructF.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int generatedInterfaces(
      passmode.DefaultStructF defaultValue,
      passmode.ValueStructF value,
      passmode.AddressStructF address) {
    if (MethodHandle$1$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: generatedInterfaces");
    try {
      return (int) MethodHandle$1$F.invokeExact(
          (java.lang.foreign.MemorySegment) (defaultValue == null ? java.lang.foreign.MemorySegment.NULL : defaultValue.MemorySegment$F),
          value.MemorySegment$F,
          (java.lang.foreign.MemorySegment) (address == null ? java.lang.foreign.MemorySegment.NULL : address.MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      SymbolLookup$F.find("originalCircular")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  passmode.CircularValue.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int originalCircular(
      passmode.CircularDefaultSpec defaultValue,
      passmode.CircularValueSpec value,
      passmode.CircularAddressSpec address) {
    if (MethodHandle$2$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: originalCircular");
    try {
      return (int) MethodHandle$2$F.invokeExact(
          (java.lang.foreign.MemorySegment) (defaultValue == null ? java.lang.foreign.MemorySegment.NULL : ((passmode.CircularDefault) defaultValue).MemorySegment$F),
          ((passmode.CircularValue) value).MemorySegment$F,
          (java.lang.foreign.MemorySegment) (address == null ? java.lang.foreign.MemorySegment.NULL : ((passmode.CircularAddress) address).MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$3$F =
      SymbolLookup$F.find("generatedCircular")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  passmode.CircularValue.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int generatedCircular(
      passmode.CircularDefault defaultValue,
      passmode.CircularValue value,
      passmode.CircularAddress address) {
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: generatedCircular");
    try {
      return (int) MethodHandle$3$F.invokeExact(
          (java.lang.foreign.MemorySegment) (defaultValue == null ? java.lang.foreign.MemorySegment.NULL : defaultValue.MemorySegment$F),
          value.MemorySegment$F,
          (java.lang.foreign.MemorySegment) (address == null ? java.lang.foreign.MemorySegment.NULL : address.MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$4$F =
      SymbolLookup$F.find("originalRecords")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  passmode.DefaultRecordF.MemoryLayout$F,
                  passmode.ValueRecordF.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int originalRecords(
      passmode.DefaultRecord defaultValue,
      passmode.ValueRecord value,
      passmode.AddressRecord address) {
    if (MethodHandle$4$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: originalRecords");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var defaultValue$allocationOffset$f = 0L;
      var allocationOffset$f = passmode.DefaultRecordF.MemoryLayout$F.byteSize();
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, passmode.ValueRecordF.MemoryLayout$F.byteAlignment()));
      var value$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, passmode.ValueRecordF.MemoryLayout$F.byteSize());
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, passmode.AddressRecordF.MemoryLayout$F.byteAlignment()));
      var address$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, (address == null ? 0L : Math.max(1L, passmode.AddressRecordF.MemoryLayout$F.byteSize())));
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, Math.max(Math.max(passmode.DefaultRecordF.MemoryLayout$F.byteAlignment(), passmode.ValueRecordF.MemoryLayout$F.byteAlignment()), passmode.AddressRecordF.MemoryLayout$F.byteAlignment()));
      var defaultValue$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          defaultValue$allocationOffset$f, passmode.DefaultRecordF.MemoryLayout$F.byteSize());
      passmode.DefaultRecordF.toMemorySegment$F(defaultValue, defaultValue$MemorySegment$f);
      var value$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          value$allocationOffset$f, passmode.ValueRecordF.MemoryLayout$F.byteSize());
      passmode.ValueRecordF.toMemorySegment$F(value, value$MemorySegment$f);
      var address$MemorySegment$f = (java.lang.foreign.MemorySegment) (address == null ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          address$allocationOffset$f, (address == null ? 0L : Math.max(1L, passmode.AddressRecordF.MemoryLayout$F.byteSize()))));
      if (address != null) {
        passmode.AddressRecordF.toMemorySegment$F(address, address$MemorySegment$f);
      }
      return (int) MethodHandle$4$F.invokeExact(
          defaultValue$MemorySegment$f,
          value$MemorySegment$f,
          address$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$5$F =
      SymbolLookup$F.find("originalOverrides")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  passmode.AddressStructF.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  passmode.AddressRecordF.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int originalOverrides(
      passmode.AddressStruct interfaceValue,
      passmode.ValueStruct interfaceAddress,
      passmode.AddressRecord recordValue,
      passmode.ValueRecord recordAddress) {
    if (MethodHandle$5$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: originalOverrides");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var recordValue$allocationOffset$f = 0L;
      var allocationOffset$f = passmode.AddressRecordF.MemoryLayout$F.byteSize();
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, passmode.ValueRecordF.MemoryLayout$F.byteAlignment()));
      var recordAddress$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, (recordAddress == null ? 0L : Math.max(1L, passmode.ValueRecordF.MemoryLayout$F.byteSize())));
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, Math.max(passmode.AddressRecordF.MemoryLayout$F.byteAlignment(), passmode.ValueRecordF.MemoryLayout$F.byteAlignment()));
      var recordValue$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          recordValue$allocationOffset$f, passmode.AddressRecordF.MemoryLayout$F.byteSize());
      passmode.AddressRecordF.toMemorySegment$F(recordValue, recordValue$MemorySegment$f);
      var recordAddress$MemorySegment$f = (java.lang.foreign.MemorySegment) (recordAddress == null ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          recordAddress$allocationOffset$f, (recordAddress == null ? 0L : Math.max(1L, passmode.ValueRecordF.MemoryLayout$F.byteSize()))));
      if (recordAddress != null) {
        passmode.ValueRecordF.toMemorySegment$F(recordAddress, recordAddress$MemorySegment$f);
      }
      return (int) MethodHandle$5$F.invokeExact(
          ((passmode.AddressStructF) interfaceValue).MemorySegment$F,
          (java.lang.foreign.MemorySegment) (interfaceAddress == null ? java.lang.foreign.MemorySegment.NULL : ((passmode.ValueStructF) interfaceAddress).MemorySegment$F),
          recordValue$MemorySegment$f,
          recordAddress$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$6$F =
      SymbolLookup$F.find("generatedOverrides")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  passmode.AddressStructF.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int generatedOverrides(
      passmode.AddressStructF interfaceValue,
      passmode.ValueStructF interfaceAddress) {
    if (MethodHandle$6$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: generatedOverrides");
    try {
      return (int) MethodHandle$6$F.invokeExact(
          interfaceValue.MemorySegment$F,
          (java.lang.foreign.MemorySegment) (interfaceAddress == null ? java.lang.foreign.MemorySegment.NULL : interfaceAddress.MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$7$F =
      SymbolLookup$F.find("originalRecordReturn")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  passmode.DefaultRecordF.MemoryLayout$F,
                  passmode.DefaultRecordF.MemoryLayout$F)))
          .orElse(null);

  public passmode.DefaultRecord originalRecordReturn(
      passmode.DefaultRecord value) {
    if (MethodHandle$7$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: originalRecordReturn");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var value$allocationOffset$f = 0L;
      var allocationOffset$f = passmode.DefaultRecordF.MemoryLayout$F.byteSize();
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, passmode.DefaultRecordF.MemoryLayout$F.byteAlignment()));
      var return$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, passmode.DefaultRecordF.MemoryLayout$F.byteSize());
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, passmode.DefaultRecordF.MemoryLayout$F.byteAlignment());
      var return$allocation$f = allocation$MemorySegment$f.asSlice(
          return$allocationOffset$f, passmode.DefaultRecordF.MemoryLayout$F.byteSize());
      var value$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          value$allocationOffset$f, passmode.DefaultRecordF.MemoryLayout$F.byteSize());
      passmode.DefaultRecordF.toMemorySegment$F(value, value$MemorySegment$f);
      return passmode.DefaultRecordF.fromMemorySegment$F((java.lang.foreign.MemorySegment) MethodHandle$7$F.invokeExact(
          (java.lang.foreign.SegmentAllocator) java.lang.foreign.SegmentAllocator.prefixAllocator(return$allocation$f),
          value$MemorySegment$f));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$8$F =
      SymbolLookup$F.find("originalRecordAddressReturn")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public passmode.@org.alveolo.ffm.Address DefaultRecord originalRecordAddressReturn(
      passmode.DefaultRecord value) {
    if (MethodHandle$8$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: originalRecordAddressReturn");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return passmode.DefaultRecordF.reinterpret$F((java.lang.foreign.MemorySegment) MethodHandle$8$F.invokeExact(
          (java.lang.foreign.MemorySegment) (value == null ? java.lang.foreign.MemorySegment.NULL : passmode.DefaultRecordF.toMemorySegment$F(arena$f, value))));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$9$F =
      SymbolLookup$F.find("originalInterfaceReturn")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public passmode.DefaultStruct originalInterfaceReturn(
      passmode.DefaultStruct value) {
    if (MethodHandle$9$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: originalInterfaceReturn");
    try {
      return passmode.DefaultStructF.reinterpret$F((java.lang.foreign.MemorySegment) MethodHandle$9$F.invokeExact(
          (java.lang.foreign.MemorySegment) (value == null ? java.lang.foreign.MemorySegment.NULL : ((passmode.DefaultStructF) value).MemorySegment$F)));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$10$F =
      SymbolLookup$F.find("originalInterfaceValueReturn")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  passmode.DefaultStructF.MemoryLayout$F,
                  passmode.DefaultStructF.MemoryLayout$F)))
          .orElse(null);

  public passmode.@org.alveolo.ffm.Value DefaultStruct originalInterfaceValueReturn(
      java.lang.foreign.SegmentAllocator allocator,
      passmode.DefaultStruct value) {
    if (MethodHandle$10$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: originalInterfaceValueReturn");
    try {
      return new passmode.DefaultStructF((java.lang.foreign.MemorySegment) MethodHandle$10$F.invokeExact(
          allocator,
          ((passmode.DefaultStructF) value).MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$11$F =
      SymbolLookup$F.find("primitiveAddress")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int primitiveAddress(
      int value) {
    if (MethodHandle$11$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: primitiveAddress");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var value$MemorySegment$f = arena$f.allocate(org.alveolo.ffm.NativeType.WCHAR_T.layout);
      org.alveolo.ffm.NativeType.setWCharT(value$MemorySegment$f, 0L, value);
      return (int) MethodHandle$11$F.invokeExact(
          value$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$12$F =
      SymbolLookup$F.find("primitiveAddressReturn")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int primitiveAddressReturn(
      ) {
    if (MethodHandle$12$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: primitiveAddressReturn");
    try {
      var addressResult$f = (java.lang.foreign.MemorySegment) MethodHandle$12$F.invokeExact(
          );
      org.alveolo.ffm.ForeignUtils.requireNonNullAddress(addressResult$f);
      return org.alveolo.ffm.NativeType.getWCharT(addressResult$f.reinterpret(org.alveolo.ffm.NativeType.WCHAR_T.layout.byteSize()), 0L);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$13$F =
      SymbolLookup$F.find("mixedRecord")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  passmode.LongRecordF.MemoryLayout$F,
                  passmode.ByteRecordF.MemoryLayout$F,
                  passmode.LongRecordF.MemoryLayout$F)))
          .orElse(null);

  public passmode.LongRecord mixedRecord(
      passmode.ByteRecord small,
      passmode.LongRecord aligned) {
    if (MethodHandle$13$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: mixedRecord");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var small$allocationOffset$f = 0L;
      var allocationOffset$f = passmode.ByteRecordF.MemoryLayout$F.byteSize();
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, passmode.LongRecordF.MemoryLayout$F.byteAlignment()));
      var aligned$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, passmode.LongRecordF.MemoryLayout$F.byteSize());
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, passmode.LongRecordF.MemoryLayout$F.byteAlignment()));
      var return$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, passmode.LongRecordF.MemoryLayout$F.byteSize());
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, Math.max(passmode.ByteRecordF.MemoryLayout$F.byteAlignment(), passmode.LongRecordF.MemoryLayout$F.byteAlignment()));
      var return$allocation$f = allocation$MemorySegment$f.asSlice(
          return$allocationOffset$f, passmode.LongRecordF.MemoryLayout$F.byteSize());
      var small$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          small$allocationOffset$f, passmode.ByteRecordF.MemoryLayout$F.byteSize());
      passmode.ByteRecordF.toMemorySegment$F(small, small$MemorySegment$f);
      var aligned$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          aligned$allocationOffset$f, passmode.LongRecordF.MemoryLayout$F.byteSize());
      passmode.LongRecordF.toMemorySegment$F(aligned, aligned$MemorySegment$f);
      return passmode.LongRecordF.fromMemorySegment$F((java.lang.foreign.MemorySegment) MethodHandle$13$F.invokeExact(
          (java.lang.foreign.SegmentAllocator) java.lang.foreign.SegmentAllocator.prefixAllocator(return$allocation$f),
          small$MemorySegment$f,
          aligned$MemorySegment$f));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$14$F =
      SymbolLookup$F.find("allocatingRecords")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  passmode.PointerRecordF.MemoryLayout$F,
                  passmode.PointerRecordF.MemoryLayout$F)))
          .orElse(null);

  public int allocatingRecords(
      passmode.PointerRecord first,
      passmode.PointerRecord second) {
    if (MethodHandle$14$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: allocatingRecords");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (int) MethodHandle$14$F.invokeExact(
          passmode.PointerRecordF.toMemorySegment$F(arena$f, first),
          passmode.PointerRecordF.toMemorySegment$F(arena$f, second));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

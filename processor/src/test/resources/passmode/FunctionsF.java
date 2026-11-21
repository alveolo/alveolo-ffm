package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
final class FunctionsF implements Functions {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.sequenceLayout(4L,
          java.lang.foreign.ValueLayout.ADDRESS);

  public static FunctionsF reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new FunctionsF(memorySegment.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public FunctionsF(java.lang.foreign.MemorySegment memorySegment) {
    this.MemorySegment$F = memorySegment;
    this.MethodHandle$0$F = DowncallHandle$0$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 0L));
    this.MethodHandle$1$F = DowncallHandle$1$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 1L));
    this.MethodHandle$2$F = DowncallHandle$2$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 2L));
    this.MethodHandle$3$F = DowncallHandle$3$F.bindTo(
        MemorySegment$F.getAtIndex(
            java.lang.foreign.ValueLayout.ADDRESS, 3L));
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$0$F =
      Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              java.lang.foreign.ValueLayout.ADDRESS,
              passmode.ValueStructF.MemoryLayout$F,
              java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$0$F;

  public int generatedInterfaces(
      passmode.DefaultStructF defaultValue,
      passmode.ValueStructF value,
      passmode.AddressStructF address) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          (java.lang.foreign.MemorySegment) (defaultValue == null ? java.lang.foreign.MemorySegment.NULL : defaultValue.MemorySegment$F),
          value.MemorySegment$F,
          (java.lang.foreign.MemorySegment) (address == null ? java.lang.foreign.MemorySegment.NULL : address.MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$1$F =
      Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              java.lang.foreign.ValueLayout.ADDRESS,
              passmode.CircularValue.MemoryLayout$F,
              java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$1$F;

  public int generatedCircular(
      passmode.CircularDefault defaultValue,
      passmode.CircularValue value,
      passmode.CircularAddress address) {
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

  private static final java.lang.invoke.MethodHandle DowncallHandle$2$F =
      Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              passmode.DefaultRecordF.MemoryLayout$F,
              passmode.ValueRecordF.MemoryLayout$F,
              java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$2$F;

  public int originalRecords(
      passmode.DefaultRecord defaultValue,
      passmode.ValueRecord value,
      passmode.AddressRecord address) {
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
      return (int) MethodHandle$2$F.invokeExact(
          defaultValue$MemorySegment$f,
          value$MemorySegment$f,
          address$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle DowncallHandle$3$F =
      Linker$F.downcallHandle(
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              passmode.AddressStructF.MemoryLayout$F,
              java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$3$F;

  public int generatedOverrides(
      passmode.AddressStructF interfaceValue,
      passmode.ValueStructF interfaceAddress) {
    try {
      return (int) MethodHandle$3$F.invokeExact(
          interfaceValue.MemorySegment$F,
          (java.lang.foreign.MemorySegment) (interfaceAddress == null ? java.lang.foreign.MemorySegment.NULL : interfaceAddress.MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

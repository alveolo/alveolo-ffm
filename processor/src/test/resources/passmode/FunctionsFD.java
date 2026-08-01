package passmode;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.DispatchTableProcessor")
public final class FunctionsFD implements Functions {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.MemoryLayout MemoryLayout$F =
      java.lang.foreign.MemoryLayout.sequenceLayout(4L,
          java.lang.foreign.ValueLayout.ADDRESS);

  public static FunctionsFD reinterpret$F(
      java.lang.foreign.MemorySegment memorySegment) {
    return new FunctionsFD(memorySegment.reinterpret(
        MemoryLayout$F.byteSize()));
  }

  public final java.lang.foreign.MemorySegment MemorySegment$F;

  public FunctionsFD(java.lang.foreign.MemorySegment memorySegment) {
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
          passmode.ValueStructFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$0$F;

  public int generatedInterfaces(
      passmode.DefaultStructFM defaultValue,
      passmode.ValueStructFM value,
      passmode.AddressStructFM address) {
    try {
      return (int) MethodHandle$0$F.invokeExact(
          defaultValue.MemorySegment$F,
          value.MemorySegment$F,
          address.MemorySegment$F);
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
          defaultValue.MemorySegment$F,
          value.MemorySegment$F,
          address.MemorySegment$F);
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
          passmode.DefaultRecordFM.MemoryLayout$F,
          passmode.ValueRecordFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$2$F;

  public int originalRecords(
      passmode.DefaultRecord defaultValue,
      passmode.ValueRecord value,
      passmode.AddressRecord address) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var defaultValue$allocationOffset$f = 0L;
      var allocationOffset$f = passmode.DefaultRecordFM.MemoryLayout$F.byteSize();
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, passmode.ValueRecordFM.MemoryLayout$F.byteAlignment()));
      var value$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, passmode.ValueRecordFM.MemoryLayout$F.byteSize());
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, passmode.AddressRecordFM.MemoryLayout$F.byteAlignment()));
      var address$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, passmode.AddressRecordFM.MemoryLayout$F.byteSize());
      var allocation$MemorySegment$f = arena$f.allocate(
          allocationOffset$f, Math.max(Math.max(passmode.DefaultRecordFM.MemoryLayout$F.byteAlignment(), passmode.ValueRecordFM.MemoryLayout$F.byteAlignment()), passmode.AddressRecordFM.MemoryLayout$F.byteAlignment()));
      var defaultValue$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          defaultValue$allocationOffset$f, passmode.DefaultRecordFM.MemoryLayout$F.byteSize());
      passmode.DefaultRecordFM.toMemorySegment$F(defaultValue, defaultValue$MemorySegment$f);
      var value$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          value$allocationOffset$f, passmode.ValueRecordFM.MemoryLayout$F.byteSize());
      passmode.ValueRecordFM.toMemorySegment$F(value, value$MemorySegment$f);
      var address$MemorySegment$f = allocation$MemorySegment$f.asSlice(
          address$allocationOffset$f, passmode.AddressRecordFM.MemoryLayout$F.byteSize());
      passmode.AddressRecordFM.toMemorySegment$F(address, address$MemorySegment$f);
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
          passmode.AddressStructFM.MemoryLayout$F,
          java.lang.foreign.ValueLayout.ADDRESS));

  private final java.lang.invoke.MethodHandle MethodHandle$3$F;

  public int generatedOverrides(
      passmode.AddressStructFM interfaceValue,
      passmode.ValueStructFM interfaceAddress) {
    try {
      return (int) MethodHandle$3$F.invokeExact(
          interfaceValue.MemorySegment$F,
          interfaceAddress.MemorySegment$F);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

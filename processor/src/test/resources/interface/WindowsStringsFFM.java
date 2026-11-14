package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class WindowsStringsFFM implements WindowsStrings {
  public static final WindowsStringsFFM INSTANCE$F = new WindowsStringsFFM();

  private WindowsStringsFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      SymbolLookup$F.find("accept")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void accept(
      java.lang.String value) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: accept");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var value$bytes$f = value == null ? null : value.getBytes(java.nio.charset.StandardCharsets.UTF_16LE);
      var value$MemorySegment$f = value == null
          ? java.lang.foreign.MemorySegment.NULL : arena$f.allocate(
          Math.addExact((long) value$bytes$f.length, 2L),
          java.lang.foreign.ValueLayout.JAVA_CHAR.byteAlignment());
      if (value != null) {
        java.lang.foreign.MemorySegment.copy(
            value$bytes$f, 0, value$MemorySegment$f,
            java.lang.foreign.ValueLayout.JAVA_BYTE, 0, value$bytes$f.length);
        value$MemorySegment$f.set(
            java.lang.foreign.ValueLayout.JAVA_CHAR, value$bytes$f.length,
            (char) 0);
      }
      MethodHandle$0$F.invokeExact(
          value$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      SymbolLookup$F.find("pair")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void pair(
      java.lang.String left,
      java.lang.String right) {
    if (MethodHandle$1$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: pair");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var left$bytes$f = left == null ? null : left.getBytes(java.nio.charset.StandardCharsets.UTF_16LE);
      var right$bytes$f = right == null ? null : right.getBytes(java.nio.charset.StandardCharsets.UTF_16LE);
      var left$allocationOffset$f = 0L;
      var allocationOffset$f = (left == null ? 0L : Math.addExact((long) left$bytes$f.length, 2L));
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, java.lang.foreign.ValueLayout.JAVA_CHAR.byteAlignment()));
      var right$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, (right == null ? 0L : Math.addExact((long) right$bytes$f.length, 2L)));
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, java.lang.foreign.ValueLayout.JAVA_CHAR.byteAlignment());
      var left$MemorySegment$f = left == null
          ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          left$allocationOffset$f, (left == null ? 0L : Math.addExact((long) left$bytes$f.length, 2L)));
      if (left != null) {
        java.lang.foreign.MemorySegment.copy(
            left$bytes$f, 0, left$MemorySegment$f,
            java.lang.foreign.ValueLayout.JAVA_BYTE, 0, left$bytes$f.length);
        left$MemorySegment$f.set(
            java.lang.foreign.ValueLayout.JAVA_CHAR, left$bytes$f.length,
            (char) 0);
      }
      var right$MemorySegment$f = right == null
          ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          right$allocationOffset$f, (right == null ? 0L : Math.addExact((long) right$bytes$f.length, 2L)));
      if (right != null) {
        java.lang.foreign.MemorySegment.copy(
            right$bytes$f, 0, right$MemorySegment$f,
            java.lang.foreign.ValueLayout.JAVA_BYTE, 0, right$bytes$f.length);
        right$MemorySegment$f.set(
            java.lang.foreign.ValueLayout.JAVA_CHAR, right$bytes$f.length,
            (char) 0);
      }
      MethodHandle$1$F.invokeExact(
          left$MemorySegment$f,
          right$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      SymbolLookup$F.find("borrowed")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public java.lang.String borrowed(
      ) {
    if (MethodHandle$2$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: borrowed");
    try {
      var stringResult$f = (java.lang.foreign.MemorySegment) MethodHandle$2$F.invokeExact(
          );
      return stringResult$f.equals(java.lang.foreign.MemorySegment.NULL) ? null
          : stringResult$f.reinterpret(Long.MAX_VALUE).getString(0L, java.nio.charset.StandardCharsets.UTF_16LE);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

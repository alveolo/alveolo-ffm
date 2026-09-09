package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class InheritedFFM implements Inherited {
  public static final InheritedFFM INSTANCE$F = new InheritedFFM();

  private InheritedFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      SymbolLookup$F.find("length")
          .map(address$f -> org.alveolo.ffm.NativeType.adaptDowncall(
              Linker$F.downcallHandle(
                  address$f,
                  java.lang.foreign.FunctionDescriptor.of(
                      org.alveolo.ffm.NativeType.SIZE_T.layout,
                      java.lang.foreign.ValueLayout.ADDRESS)),
              org.alveolo.ffm.NativeType.SIZE_T,
              new org.alveolo.ffm.NativeType[] {
                  null
              }))
          .orElse(null);

  public long length(
      java.lang.String value) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: length");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (long) MethodHandle$0$F.invokeExact(
          (java.lang.foreign.MemorySegment) (value == null ? java.lang.foreign.MemorySegment.NULL : arena$f.allocateFrom(value)));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      SymbolLookup$F.find("number")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT)))
          .orElse(null);

  public int number(
      int value) {
    if (MethodHandle$1$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: number");
    try {
      return (int) MethodHandle$1$F.invokeExact(
          value);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      SymbolLookup$F.find("shared")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_INT)))
          .orElse(null);

  public int shared(
      int value) {
    if (MethodHandle$2$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: shared");
    try {
      return (int) MethodHandle$2$F.invokeExact(
          value);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$3$F =
      SymbolLookup$F.find("reset")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  )))
          .orElse(null);

  public void reset(
      ) {
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: reset");
    try {
      MethodHandle$3$F.invokeExact(
          );
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

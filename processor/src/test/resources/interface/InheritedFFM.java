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
      org.alveolo.ffm.NativeType.adaptDowncall(
          Linker$F.downcallHandle(
              SymbolLookup$F.findOrThrow("length"),
              java.lang.foreign.FunctionDescriptor.of(
                  org.alveolo.ffm.CanonicalLayout.SIZE_T,
                  java.lang.foreign.ValueLayout.ADDRESS)),
          org.alveolo.ffm.NativeType.SIZE_T,
          new org.alveolo.ffm.NativeType[] {
              null
          });

  public long length(
      java.lang.String value) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      return (long) MethodHandle$0$F.invokeExact(
          arena$f.allocateFrom(value));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      Linker$F.downcallHandle(
          SymbolLookup$F.findOrThrow("number"),
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              java.lang.foreign.ValueLayout.JAVA_INT));

  public int number(
      int value) {
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
      Linker$F.downcallHandle(
          SymbolLookup$F.findOrThrow("shared"),
          java.lang.foreign.FunctionDescriptor.of(
              java.lang.foreign.ValueLayout.JAVA_INT,
              java.lang.foreign.ValueLayout.JAVA_INT));

  public int shared(
      int value) {
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
      Linker$F.downcallHandle(
          SymbolLookup$F.findOrThrow("reset"),
          java.lang.foreign.FunctionDescriptor.ofVoid(
              ));

  public void reset(
      ) {
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

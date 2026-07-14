package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class CoreStringsFFM implements CoreStrings {
  public static final CoreStringsFFM INSTANCE$F = new CoreStringsFFM();

  private CoreStringsFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = SymbolLookup$F();

  private static java.lang.foreign.SymbolLookup SymbolLookup$F() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        CoreStrings.class,
        Linker$F.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "CoreFoundation", "",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.FRAMEWORK));
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("CFStringGetLength"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS));

  public long CFStringGetLength(
      java.lang.String value) {
    java.lang.foreign.MemorySegment value$CFString$f = java.lang.foreign.MemorySegment.NULL;
    try {
      value$CFString$f = org.alveolo.ffm.macos.CFStringSupport.toCFString(value);
      return (long) MethodHandle$0$F.invokeExact(
          value$CFString$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    } finally {
      org.alveolo.ffm.macos.CFStringSupport.release(value$CFString$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("CFStringCompare"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_LONG));

  public long compare(
      java.lang.String left,
      java.lang.String right,
      long options) {
    java.lang.foreign.MemorySegment left$CFString$f = java.lang.foreign.MemorySegment.NULL;
    java.lang.foreign.MemorySegment right$CFString$f = java.lang.foreign.MemorySegment.NULL;
    try {
      left$CFString$f = org.alveolo.ffm.macos.CFStringSupport.toCFString(left);
      right$CFString$f = org.alveolo.ffm.macos.CFStringSupport.toCFString(right);
      return (long) MethodHandle$1$F.invokeExact(
          left$CFString$f,
          right$CFString$f,
          options);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    } finally {
      org.alveolo.ffm.macos.CFStringSupport.release(left$CFString$f);
      org.alveolo.ffm.macos.CFStringSupport.release(right$CFString$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("CFStringCreateWithCString"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public java.lang.String create(
      java.lang.foreign.MemorySegment allocator,
      java.lang.String cString,
      int encoding) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var cfStringResult$f = (java.lang.foreign.MemorySegment) MethodHandle$2$F.invokeExact(
          allocator,
          arena$f.allocateFrom(cString),
          encoding);
      try {
        return org.alveolo.ffm.macos.CFStringSupport
            .toJavaString(cfStringResult$f);
      } finally {
        org.alveolo.ffm.macos.CFStringSupport.release(cfStringResult$f);
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}

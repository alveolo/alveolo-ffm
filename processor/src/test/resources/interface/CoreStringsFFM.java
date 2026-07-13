package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class CoreStringsFFM implements CoreStrings {
  public static final CoreStringsFFM INSTANCE = new CoreStringsFFM();

  private CoreStringsFFM() {}

  public static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup FF$LOOKUP = FF$LOOKUP();

  private static java.lang.foreign.SymbolLookup FF$LOOKUP() {
    return org.alveolo.ffm.ForeignUtils.libraryLookup(
        CoreStrings.class,
        FF$LINKER.defaultLookup(),
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            "CoreFoundation", "",
            new org.alveolo.ffm.Library.OS[] {},
            org.alveolo.ffm.Library.Kind.FRAMEWORK));
  }

  private static final java.lang.invoke.MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("CFStringGetLength"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS));

  public long CFStringGetLength(
      java.lang.String value) {
    java.lang.foreign.MemorySegment ff$cfString$value = java.lang.foreign.MemorySegment.NULL;
    try {
      ff$cfString$value = org.alveolo.ffm.macos.CFStringSupport.toCFString(value);
      return (long) FF$MH$0.invokeExact(
          ff$cfString$value);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    } finally {
      org.alveolo.ffm.macos.CFStringSupport.release(ff$cfString$value);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$1 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("CFStringCompare"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_LONG,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_LONG));

  public long compare(
      java.lang.String left,
      java.lang.String right,
      long options) {
    java.lang.foreign.MemorySegment ff$cfString$left = java.lang.foreign.MemorySegment.NULL;
    java.lang.foreign.MemorySegment ff$cfString$right = java.lang.foreign.MemorySegment.NULL;
    try {
      ff$cfString$left = org.alveolo.ffm.macos.CFStringSupport.toCFString(left);
      ff$cfString$right = org.alveolo.ffm.macos.CFStringSupport.toCFString(right);
      return (long) FF$MH$1.invokeExact(
          ff$cfString$left,
          ff$cfString$right,
          options);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    } finally {
      org.alveolo.ffm.macos.CFStringSupport.release(ff$cfString$left);
      org.alveolo.ffm.macos.CFStringSupport.release(ff$cfString$right);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$2 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("CFStringCreateWithCString"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public java.lang.String create(
      java.lang.foreign.MemorySegment allocator,
      java.lang.String cString,
      int encoding) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$CFString$r = (java.lang.foreign.MemorySegment) FF$MH$2.invokeExact(
          allocator,
          ff$arena.allocateFrom(cString),
          encoding);
      try {
        return org.alveolo.ffm.macos.CFStringSupport
            .toJavaString(ff$CFString$r);
      } finally {
        org.alveolo.ffm.macos.CFStringSupport.release(ff$CFString$r);
      }
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}

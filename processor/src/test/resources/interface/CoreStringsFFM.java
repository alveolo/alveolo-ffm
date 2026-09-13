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
      SymbolLookup$F.find("CFStringCreateWithCharacters")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.JAVA_LONG)))
          .orElse(null);

  public java.lang.foreign.MemorySegment CFStringCreateWithCharacters(
      java.lang.foreign.MemorySegment arg0,
      java.lang.foreign.MemorySegment arg1,
      long arg2) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFStringCreateWithCharacters");
    try {
      return (java.lang.foreign.MemorySegment) MethodHandle$0$F.invokeExact(
          arg0,
          arg1,
          arg2);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      SymbolLookup$F.find("CFStringGetLength")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_LONG,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public long CFStringGetLength(
      java.lang.foreign.MemorySegment arg0) {
    if (MethodHandle$1$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFStringGetLength");
    try {
      return (long) MethodHandle$1$F.invokeExact(
          arg0);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      SymbolLookup$F.find("CFStringGetCharacters")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  org.alveolo.ffm.macos.CFRange.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void CFStringGetCharacters(
      java.lang.foreign.MemorySegment arg0,
      org.alveolo.ffm.macos.CFRange arg1,
      java.lang.foreign.MemorySegment arg2) {
    if (MethodHandle$2$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFStringGetCharacters");
    try {
      MethodHandle$2$F.invokeExact(
          arg0,
          arg1.MemorySegment$F,
          arg2);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$3$F =
      SymbolLookup$F.find("CFRelease")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void CFRelease(
      java.lang.foreign.MemorySegment arg0) {
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFRelease");
    try {
      MethodHandle$3$F.invokeExact(
          arg0);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$4$F =
      SymbolLookup$F.find("CFStringGetLength")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_LONG,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public long CFStringGetLength(
      java.lang.String value) {
    if (MethodHandle$4$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFStringGetLength");
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFRelease");
    java.lang.foreign.MemorySegment value$CFString$f = java.lang.foreign.MemorySegment.NULL;
    try {
      value$CFString$f = toCFString(value);
      return (long) MethodHandle$4$F.invokeExact(
          value$CFString$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    } finally {
      if (!value$CFString$f.equals(java.lang.foreign.MemorySegment.NULL))
        CFRelease(value$CFString$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$5$F =
      SymbolLookup$F.find("CFStringGetBytes")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_LONG,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  org.alveolo.ffm.macos.CFRange.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.JAVA_BYTE,
                  java.lang.foreign.ValueLayout.JAVA_BOOLEAN,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.JAVA_LONG,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public long CFStringGetBytes(
      java.lang.String value,
      org.alveolo.ffm.macos.CFRange range,
      int encoding,
      byte lossByte,
      boolean externalRepresentation,
      @org.alveolo.ffm.Out byte[] buffer,
      long capacity,
      @org.alveolo.ffm.Out long[] used) {
    if (MethodHandle$5$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFStringGetBytes");
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFRelease");
    java.lang.foreign.MemorySegment value$CFString$f = java.lang.foreign.MemorySegment.NULL;
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var buffer$size$f = buffer == null ? 0 : buffer.length;
      var used$size$f = used == null ? 0 : used.length;
      var buffer$allocationOffset$f = 0L;
      var allocationOffset$f = (buffer == null ? 0L : Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_BYTE.byteSize(), (long) buffer$size$f)));
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, java.lang.foreign.ValueLayout.JAVA_LONG.byteAlignment()));
      var used$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, (used == null ? 0L : Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_LONG.byteSize(), (long) used$size$f))));
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, Math.max(java.lang.foreign.ValueLayout.JAVA_BYTE.byteAlignment(), java.lang.foreign.ValueLayout.JAVA_LONG.byteAlignment()));
      value$CFString$f = toCFString(value);
      var buffer$MemorySegment$f = buffer == null
          ? java.lang.foreign.MemorySegment.NULL
          : allocation$MemorySegment$f.asSlice(
              buffer$allocationOffset$f, (buffer == null ? 0L : Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_BYTE.byteSize(), (long) buffer$size$f))));
      var used$MemorySegment$f = used == null
          ? java.lang.foreign.MemorySegment.NULL
          : allocation$MemorySegment$f.asSlice(
              used$allocationOffset$f, (used == null ? 0L : Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_LONG.byteSize(), (long) used$size$f))));
      var result$f = (long) MethodHandle$5$F.invokeExact(
          value$CFString$f,
          range.MemorySegment$F,
          encoding,
          lossByte,
          externalRepresentation,
          buffer$MemorySegment$f,
          capacity,
          used$MemorySegment$f);
      if (buffer$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            buffer$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_BYTE, 0, buffer, 0, buffer$size$f);
      }
      if (used$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            used$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_LONG, 0, used, 0, used$size$f);
      }
      return result$f;
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    } finally {
      if (!value$CFString$f.equals(java.lang.foreign.MemorySegment.NULL))
        CFRelease(value$CFString$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$6$F =
      SymbolLookup$F.find("CFStringCompare")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_LONG,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.JAVA_LONG)))
          .orElse(null);

  public long compare(
      java.lang.String left,
      java.lang.String right,
      long options) {
    if (MethodHandle$6$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFStringCompare");
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFRelease");
    java.lang.foreign.MemorySegment left$CFString$f = java.lang.foreign.MemorySegment.NULL;
    java.lang.foreign.MemorySegment right$CFString$f = java.lang.foreign.MemorySegment.NULL;
    try {
      left$CFString$f = toCFString(left);
      right$CFString$f = toCFString(right);
      return (long) MethodHandle$6$F.invokeExact(
          left$CFString$f,
          right$CFString$f,
          options);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    } finally {
      if (!left$CFString$f.equals(java.lang.foreign.MemorySegment.NULL))
        CFRelease(left$CFString$f);
      if (!right$CFString$f.equals(java.lang.foreign.MemorySegment.NULL))
        CFRelease(right$CFString$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$7$F =
      SymbolLookup$F.find("CFStringCreateWithCString")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.JAVA_INT)))
          .orElse(null);

  public java.lang.String create(
      java.lang.foreign.MemorySegment allocator,
      java.lang.String cString,
      int encoding) {
    if (MethodHandle$7$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFStringCreateWithCString");
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFRelease");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var nativeResult$f = (java.lang.foreign.MemorySegment) MethodHandle$7$F.invokeExact(
          allocator,
          (java.lang.foreign.MemorySegment) (cString == null ? java.lang.foreign.MemorySegment.NULL : arena$f.allocateFrom(cString)),
          encoding);
      try (var release$f = (java.lang.AutoCloseable) () -> {
        if (!nativeResult$f.equals(java.lang.foreign.MemorySegment.NULL))
          CFRelease(nativeResult$f);
      }) {
        return fromCFString(nativeResult$f);
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$8$F =
      SymbolLookup$F.find("CFStringFindWithOptions")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_BOOLEAN,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  org.alveolo.ffm.macos.CFRange.MemoryLayout$F,
                  java.lang.foreign.ValueLayout.JAVA_LONG,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public boolean CFStringFindWithOptions(
      java.lang.String value,
      java.lang.String stringToFind,
      org.alveolo.ffm.macos.CFRange range,
      long options,
      org.alveolo.ffm.macos.CFRange result) {
    if (MethodHandle$8$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFStringFindWithOptions");
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: CFRelease");
    java.lang.foreign.MemorySegment value$CFString$f = java.lang.foreign.MemorySegment.NULL;
    java.lang.foreign.MemorySegment stringToFind$CFString$f = java.lang.foreign.MemorySegment.NULL;
    try {
      value$CFString$f = toCFString(value);
      stringToFind$CFString$f = toCFString(stringToFind);
      return (boolean) MethodHandle$8$F.invokeExact(
          value$CFString$f,
          stringToFind$CFString$f,
          range.MemorySegment$F,
          options,
          (java.lang.foreign.MemorySegment) (result == null ? java.lang.foreign.MemorySegment.NULL : result.MemorySegment$F));
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    } finally {
      if (!value$CFString$f.equals(java.lang.foreign.MemorySegment.NULL))
        CFRelease(value$CFString$f);
      if (!stringToFind$CFString$f.equals(java.lang.foreign.MemorySegment.NULL))
        CFRelease(stringToFind$CFString$f);
    }
  }
}

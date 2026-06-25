package org.alveolo.ffm.macos;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

/// Small CFString helpers used by generated macOS bindings.
public final class CFStringSupport {
  public static final int kCFStringEncodingUTF8 = 0x08000100;

  private static final Linker LINKER = Linker.nativeLinker();

  private static final SymbolLookup LOOKUP = SymbolLookup.libraryLookup(
      "/System/Library/Frameworks/CoreFoundation.framework/CoreFoundation",
      Arena.global());

  private static final MethodHandle CFStringCreateWithCString =
      LINKER.downcallHandle(
          LOOKUP.find("CFStringCreateWithCString").get(),
          FunctionDescriptor.of(ValueLayout.ADDRESS,
              ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));

  private static final MethodHandle CFStringGetCStringPtr =
      LINKER.downcallHandle(
          LOOKUP.find("CFStringGetCStringPtr").get(),
          FunctionDescriptor.of(ValueLayout.ADDRESS,
              ValueLayout.ADDRESS, ValueLayout.JAVA_INT));

  private static final MethodHandle CFStringGetLength =
      LINKER.downcallHandle(
          LOOKUP.find("CFStringGetLength").get(),
          FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

  private static final MethodHandle CFStringGetMaximumSizeForEncoding =
      LINKER.downcallHandle(
          LOOKUP.find("CFStringGetMaximumSizeForEncoding").get(),
          FunctionDescriptor.of(ValueLayout.JAVA_LONG,
              ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT));

  private static final MethodHandle CFStringGetCString =
      LINKER.downcallHandle(
          LOOKUP.find("CFStringGetCString").get(),
          FunctionDescriptor.of(ValueLayout.JAVA_BYTE,
              ValueLayout.ADDRESS, ValueLayout.ADDRESS,
              ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT));

  private static final MethodHandle CFRelease =
      LINKER.downcallHandle(
          LOOKUP.find("CFRelease").get(),
          FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

  private CFStringSupport() {}

  public static MemorySegment toCFString(String value) {
    if (value == null) return MemorySegment.NULL;

    try (var arena = Arena.ofConfined()) {
      return (MemorySegment) CFStringCreateWithCString.invokeExact(
          MemorySegment.NULL,
          arena.allocateFrom(value, UTF_8), kCFStringEncodingUTF8);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new AssertionError(t);
    }
  }

  public static String toJavaString(MemorySegment value) {
    if (isNull(value)) return null;

    try {
      var cString = (MemorySegment) CFStringGetCStringPtr.invokeExact(
          value, kCFStringEncodingUTF8);
      if (!isNull(cString))
        return cString.reinterpret(Long.MAX_VALUE).getString(0L, UTF_8);

      var length = (long) CFStringGetLength.invokeExact(value);

      var maxBytes = (long) CFStringGetMaximumSizeForEncoding.invokeExact(
          length, kCFStringEncodingUTF8);
      if (maxBytes < 0)
        throw new IllegalArgumentException(
            "CFString cannot be encoded as UTF-8");

      try (var arena = Arena.ofConfined()) {
        var buffer = arena.allocate(maxBytes + 1L, 1L);

        var ok = ((byte) CFStringGetCString.invokeExact(
            value, buffer, maxBytes + 1L, kCFStringEncodingUTF8)) != 0;
        if (!ok)
          throw new IllegalArgumentException(
              "CFString cannot be encoded as UTF-8");

        return buffer.getString(0L, UTF_8);
      }
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new AssertionError(t);
    }
  }

  public static void release(MemorySegment value) {
    if (isNull(value)) return;

    try {
      CFRelease.invokeExact(value);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new AssertionError(t);
    }
  }

  private static boolean isNull(MemorySegment value) {
    return value == null || value.address() == 0L;
  }
}

package org.alveolo.ffm.macos;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
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

  private static final MethodHandle CFStringCreateWithCharacters =
      LINKER.downcallHandle(
          LOOKUP.findOrThrow("CFStringCreateWithCharacters"),
          FunctionDescriptor.of(ValueLayout.ADDRESS,
              ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG));

  private static final MethodHandle CFStringGetLength =
      LINKER.downcallHandle(
          LOOKUP.findOrThrow("CFStringGetLength"),
          FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

  private static final MethodHandle CFStringGetCharacters =
      LINKER.downcallHandle(
          LOOKUP.findOrThrow("CFStringGetCharacters"),
          FunctionDescriptor.ofVoid(ValueLayout.ADDRESS,
              MemoryLayout.structLayout(
                  ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG),
              ValueLayout.ADDRESS));

  private static final MethodHandle CFRelease =
      LINKER.downcallHandle(
          LOOKUP.findOrThrow("CFRelease"),
          FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

  private CFStringSupport() {}

  public static MemorySegment toCFString(String value) {
    if (value == null) return MemorySegment.NULL;

    try (var arena = Arena.ofConfined()) {
      return (MemorySegment) CFStringCreateWithCharacters.invokeExact(
          MemorySegment.NULL,
          arena.allocateFrom(ValueLayout.JAVA_CHAR, value.toCharArray()),
          (long) value.length());
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new AssertionError(t);
    }
  }

  public static String toJavaString(MemorySegment value) {
    if (isNull(value)) return null;

    try (var arena = Arena.ofConfined()) {
      var length = Math.toIntExact((long) CFStringGetLength.invokeExact(value));
      if (length == 0) return "";

      var buffer = arena.allocate(ValueLayout.JAVA_CHAR, length);
      var range = arena.allocateFrom(ValueLayout.JAVA_LONG, 0L, (long) length);
      CFStringGetCharacters.invokeExact(value, range, buffer);
      return new String(buffer.toArray(ValueLayout.JAVA_CHAR));
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
    return value == null || value.equals(MemorySegment.NULL);
  }
}

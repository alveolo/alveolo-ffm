package org.alveolo.ffm;

import static java.lang.foreign.Linker.nativeLinker;
import static java.lang.invoke.MethodHandles.identity;
import static java.lang.invoke.MethodType.methodType;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

/// Runtime support for platform-dependent canonical C scalar types.
///
/// Generated bindings use this class; application code normally only needs
/// [SLong], [ULong], [SizeT], and [WCharT]. Keeping this state separate from
/// [ForeignUtils] avoids initializing the native linker for fixed-layout structs
/// that only use padding helpers.
public final class NativeTypes {
  public enum Type {
    SLONG(C_LONG_LAYOUT, long.class),
    ULONG(C_LONG_LAYOUT, long.class),
    WCHAR_T(WCHAR_T_LAYOUT, int.class);

    private final ValueLayout layout;
    private final Class<?> javaCarrier;

    Type(ValueLayout layout, Class<?> javaCarrier) {
      this.layout = layout;
      this.javaCarrier = javaCarrier;
    }
  }

  public static final ValueLayout C_LONG_LAYOUT = canonicalLayout("long");
  public static final ValueLayout.OfLong SIZE_T_LAYOUT =
      (ValueLayout.OfLong) nativeLinker().canonicalLayouts().get("size_t");
  public static final ValueLayout WCHAR_T_LAYOUT = canonicalLayout("wchar_t");

  private static final MethodHandle LONG_TO_SIGNED_INT_EXACT = methodHandle(
      Math.class, "toIntExact", methodType(int.class, long.class));
  private static final MethodHandle INT_TO_SIGNED_LONG =
      identity(long.class).asType(methodType(long.class, int.class));
  private static final MethodHandle LONG_TO_UNSIGNED_INT_EXACT = methodHandle(
      NativeTypes.class, "longToUnsignedIntExact",
      methodType(int.class, long.class));
  private static final MethodHandle INT_TO_UNSIGNED_LONG = methodHandle(
      Integer.class, "toUnsignedLong", methodType(long.class, int.class));
  private static final MethodHandle INT_TO_CHAR_EXACT = methodHandle(
      NativeTypes.class, "intToCharExact", methodType(char.class, int.class));
  private static final MethodHandle CHAR_TO_INT = methodHandle(
      NativeTypes.class, "charToInt", methodType(int.class, char.class));

  private static final MethodHandle SLONG_GET = adaptGetter(
      C_LONG_LAYOUT.varHandle(), Type.SLONG);
  private static final MethodHandle SLONG_SET = adaptSetter(
      C_LONG_LAYOUT.varHandle(), Type.SLONG);
  private static final MethodHandle ULONG_GET = adaptGetter(
      C_LONG_LAYOUT.varHandle(), Type.ULONG);
  private static final MethodHandle ULONG_SET = adaptSetter(
      C_LONG_LAYOUT.varHandle(), Type.ULONG);
  private static final MethodHandle WCHAR_T_GET = adaptGetter(
      WCHAR_T_LAYOUT.varHandle(), Type.WCHAR_T);
  private static final MethodHandle WCHAR_T_SET = adaptSetter(
      WCHAR_T_LAYOUT.varHandle(), Type.WCHAR_T);

  private NativeTypes() {/* Utility class */}

  /// Adapts a raw downcall handle to stable Java carriers.
  ///
  /// `argumentTypes` follows the raw handle parameter list, including linker
  /// parameters such as an unbound function address, return allocator, or
  /// captured-call-state segment. Use `null` for parameters that do not need a
  /// canonical scalar adaptation.
  public static MethodHandle adaptDowncall(MethodHandle target,
      Type returnType, Type... argumentTypes) {
    var targetType = target.type();
    if (argumentTypes.length != targetType.parameterCount())
      throw new IllegalArgumentException(
          "Expected " + targetType.parameterCount()
              + " canonical argument types, got " + argumentTypes.length);

    for (var index = 0; index < argumentTypes.length; index++) {
      var canonical = argumentTypes[index];
      if (canonical == null) {
        continue;
      }

      var nativeCarrier = target.type().parameterType(index);
      verifyNativeCarrier(canonical, nativeCarrier);
      var filter = argumentFilter(canonical, nativeCarrier);
      if (filter != null) {
        target = MethodHandles.filterArguments(target, index, filter);
      }
    }

    if (returnType != null) {
      var nativeCarrier = target.type().returnType();
      verifyNativeCarrier(returnType, nativeCarrier);
      var filter = returnFilter(returnType, nativeCarrier);
      if (filter != null) {
        target = MethodHandles.filterReturnValue(target, filter);
      }
    }

    return target;
  }

  /// Converts a VarHandle getter to a MethodHandle with a stable Java return
  /// carrier. Coordinates are preserved unchanged.
  public static MethodHandle adaptGetter(VarHandle target, Type type) {
    var getter = target.toMethodHandle(VarHandle.AccessMode.GET);
    var nativeCarrier = getter.type().returnType();
    verifyNativeCarrier(type, nativeCarrier);
    var filter = returnFilter(type, nativeCarrier);
    return filter == null
        ? getter : MethodHandles.filterReturnValue(getter, filter);
  }

  /// Converts a VarHandle setter to a MethodHandle with a stable Java value
  /// carrier. Coordinates are preserved unchanged.
  public static MethodHandle adaptSetter(VarHandle target, Type type) {
    var setter = target.toMethodHandle(VarHandle.AccessMode.SET);
    var valueIndex = setter.type().parameterCount() - 1;
    var nativeCarrier = setter.type().parameterType(valueIndex);
    verifyNativeCarrier(type, nativeCarrier);
    var filter = argumentFilter(type, nativeCarrier);
    return filter == null
        ? setter : MethodHandles.filterArguments(setter, valueIndex, filter);
  }

  public static long getSLong(MemorySegment segment, long offset) {
    try {
      return (long) SLONG_GET.invokeExact(segment, offset);
    } catch (RuntimeException | Error exception) {
      throw exception;
    } catch (Throwable throwable) {
      throw new AssertionError(throwable);
    }
  }

  public static void setSLong(
      MemorySegment segment, long offset, long value) {
    try {
      SLONG_SET.invokeExact(segment, offset, value);
    } catch (RuntimeException | Error exception) {
      throw exception;
    } catch (Throwable throwable) {
      throw new AssertionError(throwable);
    }
  }

  public static long getULong(MemorySegment segment, long offset) {
    try {
      return (long) ULONG_GET.invokeExact(segment, offset);
    } catch (RuntimeException | Error exception) {
      throw exception;
    } catch (Throwable throwable) {
      throw new AssertionError(throwable);
    }
  }

  public static void setULong(
      MemorySegment segment, long offset, long value) {
    try {
      ULONG_SET.invokeExact(segment, offset, value);
    } catch (RuntimeException | Error exception) {
      throw exception;
    } catch (Throwable throwable) {
      throw new AssertionError(throwable);
    }
  }

  public static int getWCharT(MemorySegment segment, long offset) {
    try {
      return (int) WCHAR_T_GET.invokeExact(segment, offset);
    } catch (RuntimeException | Error exception) {
      throw exception;
    } catch (Throwable throwable) {
      throw new AssertionError(throwable);
    }
  }

  public static void setWCharT(
      MemorySegment segment, long offset, int value) {
    try {
      WCHAR_T_SET.invokeExact(segment, offset, value);
    } catch (RuntimeException | Error exception) {
      throw exception;
    } catch (Throwable throwable) {
      throw new AssertionError(throwable);
    }
  }

  public static int longToUnsignedIntExact(long value) {
    if (value < 0 || value > 0xffff_ffffL) throw new ArithmeticException(
        "unsigned long value does not fit 32 bits: " + value);
    return (int) value;
  }

  public static char intToCharExact(int value) {
    if ((value & 0xffff_0000) != 0) throw new ArithmeticException(
        "wchar_t value does not fit 16 bits: " + value);
    return (char) value;
  }

  public static int charToInt(char value) {
    return value;
  }

  private static ValueLayout canonicalLayout(String name) {
    var layout = nativeLinker().canonicalLayouts().get(name);
    if (layout instanceof ValueLayout valueLayout) return valueLayout;
    throw new ExceptionInInitializerError(
        "Native linker has no scalar canonical layout for " + name);
  }

  private static MethodHandle argumentFilter(
      Type type, Class<?> nativeCarrier) {
    if (nativeCarrier == type.javaCarrier) return null;

    return switch (type) {
      case SLONG -> LONG_TO_SIGNED_INT_EXACT;
      case ULONG -> LONG_TO_UNSIGNED_INT_EXACT;
      case WCHAR_T -> INT_TO_CHAR_EXACT;
    };
  }

  private static MethodHandle returnFilter(
      Type type, Class<?> nativeCarrier) {
    if (nativeCarrier == type.javaCarrier) return null;

    return switch (type) {
      case SLONG -> INT_TO_SIGNED_LONG;
      case ULONG -> INT_TO_UNSIGNED_LONG;
      case WCHAR_T -> CHAR_TO_INT;
    };
  }

  private static void verifyNativeCarrier(
      Type type, Class<?> actualCarrier) {
    var expectedCarrier = type.layout.carrier();
    if (actualCarrier != expectedCarrier) throw new IllegalArgumentException(
        type + " expects native carrier " + expectedCarrier.getName()
            + ", got " + actualCarrier.getName());
  }

  private static MethodHandle methodHandle(
      Class<?> owner, String methodName, MethodType type) {
    try {
      return MethodHandles.publicLookup()
          .findStatic(owner, methodName, type);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

}

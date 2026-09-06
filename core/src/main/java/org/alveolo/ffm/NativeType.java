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
public enum NativeType {
  SLONG(canonicalLayout("long"), long.class),
  ULONG(canonicalLayout("long"), long.class),
  SIZE_T(canonicalLayout("size_t"), long.class),
  WCHAR_T(canonicalLayout("wchar_t"), int.class);

  public final ValueLayout layout;
  private final Class<?> carrier;

  NativeType(ValueLayout layout, Class<?> carrier) {
    this.layout = layout;
    this.carrier = carrier;
  }

  private static final MethodHandle LONG_TO_SIGNED_INT_EXACT = methodHandle(
      Math.class, "toIntExact", methodType(int.class, long.class));
  private static final MethodHandle INT_TO_SIGNED_LONG =
      identity(long.class).asType(methodType(long.class, int.class));
  private static final MethodHandle LONG_TO_UNSIGNED_INT_EXACT = methodHandle(
      NativeType.class, "longToUnsignedIntExact",
      methodType(int.class, long.class));
  private static final MethodHandle INT_TO_UNSIGNED_LONG = methodHandle(
      Integer.class, "toUnsignedLong", methodType(long.class, int.class));
  private static final MethodHandle INT_TO_CHAR_EXACT = methodHandle(
      NativeType.class, "intToCharExact", methodType(char.class, int.class));
  private static final MethodHandle CHAR_TO_INT = methodHandle(
      NativeType.class, "charToInt", methodType(int.class, char.class));

  private static final MethodHandle SLONG_GET =
      SLONG.adaptGetter(SLONG.layout.varHandle());
  private static final MethodHandle SLONG_SET =
      SLONG.adaptSetter(SLONG.layout.varHandle());
  private static final MethodHandle ULONG_GET =
      ULONG.adaptGetter(ULONG.layout.varHandle());
  private static final MethodHandle ULONG_SET =
      ULONG.adaptSetter(ULONG.layout.varHandle());
  private static final MethodHandle SIZE_T_GET =
      SIZE_T.adaptGetter(SIZE_T.layout.varHandle());
  private static final MethodHandle SIZE_T_SET =
      SIZE_T.adaptSetter(SIZE_T.layout.varHandle());
  private static final MethodHandle WCHAR_T_GET =
      WCHAR_T.adaptGetter(WCHAR_T.layout.varHandle());
  private static final MethodHandle WCHAR_T_SET =
      WCHAR_T.adaptSetter(WCHAR_T.layout.varHandle());

  /// Adapts a raw downcall handle to stable Java carriers.
  ///
  /// `argumentTypes` follows the raw handle parameter list, including linker
  /// parameters such as an unbound function address, return allocator, or
  /// captured-call-state segment. Use `null` for parameters that do not need a
  /// canonical scalar adaptation.
  public static MethodHandle adaptDowncall(MethodHandle target,
      NativeType returnType, NativeType... argumentTypes) {
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
      canonical.verifyNativeCarrier(nativeCarrier);
      var filter = canonical.argumentFilter(nativeCarrier);
      if (filter != null) {
        target = MethodHandles.filterArguments(target, index, filter);
      }
    }

    if (returnType != null) {
      var nativeCarrier = target.type().returnType();
      returnType.verifyNativeCarrier(nativeCarrier);
      var filter = returnType.returnFilter(nativeCarrier);
      if (filter != null) {
        target = MethodHandles.filterReturnValue(target, filter);
      }
    }

    return target;
  }

  /// Converts a VarHandle getter to a MethodHandle with a stable Java return
  /// carrier. Coordinates are preserved unchanged.
  public MethodHandle adaptGetter(VarHandle target) {
    var getter = target.toMethodHandle(VarHandle.AccessMode.GET);
    var nativeCarrier = getter.type().returnType();
    verifyNativeCarrier(nativeCarrier);
    var filter = returnFilter(nativeCarrier);
    return filter == null
        ? getter : MethodHandles.filterReturnValue(getter, filter);
  }

  /// Converts a VarHandle setter to a MethodHandle with a stable Java value
  /// carrier. Coordinates are preserved unchanged.
  public MethodHandle adaptSetter(VarHandle target) {
    var setter = target.toMethodHandle(VarHandle.AccessMode.SET);
    var valueIndex = setter.type().parameterCount() - 1;
    var nativeCarrier = setter.type().parameterType(valueIndex);
    verifyNativeCarrier(nativeCarrier);
    var filter = argumentFilter(nativeCarrier);
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

  public static long getSizeT(MemorySegment segment, long offset) {
    try {
      return (long) SIZE_T_GET.invokeExact(segment, offset);
    } catch (RuntimeException | Error exception) {
      throw exception;
    } catch (Throwable throwable) {
      throw new AssertionError(throwable);
    }
  }

  public static void setSizeT(
      MemorySegment segment, long offset, long value) {
    try {
      SIZE_T_SET.invokeExact(segment, offset, value);
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
        "unsigned value does not fit 32 bits: " + value);
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
    return (ValueLayout) nativeLinker().canonicalLayouts().get(name);
  }

  private MethodHandle argumentFilter(Class<?> nativeCarrier) {
    if (nativeCarrier == carrier) return null;

    return switch (this) {
      case SLONG -> LONG_TO_SIGNED_INT_EXACT;
      case ULONG, SIZE_T -> LONG_TO_UNSIGNED_INT_EXACT;
      case WCHAR_T -> INT_TO_CHAR_EXACT;
    };
  }

  private MethodHandle returnFilter(Class<?> nativeCarrier) {
    if (nativeCarrier == carrier) return null;

    return switch (this) {
      case SLONG -> INT_TO_SIGNED_LONG;
      case ULONG, SIZE_T -> INT_TO_UNSIGNED_LONG;
      case WCHAR_T -> CHAR_TO_INT;
    };
  }

  private void verifyNativeCarrier(Class<?> actualCarrier) {
    var expectedCarrier = layout.carrier();
    if (actualCarrier != expectedCarrier)
      throw new IllegalArgumentException(
          this + " expects native carrier " + expectedCarrier.getName()
              + ", got " + actualCarrier.getName());
  }

  private static MethodHandle methodHandle(
      Class<?> owner, String methodName, MethodType type) {
    try {
      return MethodHandles.publicLookup().findStatic(owner, methodName, type);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}

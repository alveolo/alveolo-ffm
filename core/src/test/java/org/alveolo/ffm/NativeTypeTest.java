package org.alveolo.ffm;

import static java.lang.invoke.MethodHandles.identity;
import static org.alveolo.ffm.CanonicalLayout.LONG;
import static org.alveolo.ffm.NativeType.SLONG;
import static org.alveolo.ffm.NativeType.ULONG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodType;

import org.junit.jupiter.api.Test;

class NativeTypeTest {
  @Test
  void adaptsNativeScalarHandleCarriers() throws Throwable {
    var sLongRaw = identity(LONG.carrier());
    var sLong = NativeType.adaptDowncall(sLongRaw, SLONG, SLONG);
    assertEquals(MethodType.methodType(long.class, long.class), sLong.type());
    assertEquals(-123L, (long) sLong.invokeExact(-123L));
    if (LONG.carrier() == long.class) {
      assertSame(sLongRaw, sLong);
    } else {
      assertThrows(ArithmeticException.class, () -> {
        var ignored = (long) sLong.invokeExact(0x8000_0000L);
      });
    }

    var uLongRaw = identity(LONG.carrier());
    var uLong = NativeType.adaptDowncall(uLongRaw, ULONG, ULONG);
    assertEquals(MethodType.methodType(long.class, long.class), uLong.type());
    assertEquals(0xffff_ffffL, (long) uLong.invokeExact(0xffff_ffffL));
    if (LONG.carrier() == long.class) {
      assertSame(uLongRaw, uLong);
    } else {
      assertThrows(ArithmeticException.class, () -> {
        var ignored = (long) uLong.invokeExact(-1L);
      });
      assertThrows(ArithmeticException.class, () -> {
        var ignored = (long) uLong.invokeExact(0x1_0000_0000L);
      });
    }

    assertEquals(-1,
        NativeType.longToUnsignedIntExact(0xffff_ffffL));
    assertThrows(ArithmeticException.class,
        () -> NativeType.longToUnsignedIntExact(-1L));
    assertThrows(ArithmeticException.class,
        () -> NativeType.longToUnsignedIntExact(0x1_0000_0000L));

    assertSame(long.class, CanonicalLayout.SIZE_T.carrier());

    var wcharRaw = identity(CanonicalLayout.WCHAR_T.carrier());
    var wchar = NativeType.adaptDowncall(
        wcharRaw, NativeType.WCHAR,
        NativeType.WCHAR);
    assertEquals(MethodType.methodType(int.class, int.class), wchar.type());
    assertEquals(0xffff, (int) wchar.invokeExact(0xffff));
    if (CanonicalLayout.WCHAR_T.carrier() == int.class) {
      assertSame(wcharRaw, wchar);
    } else {
      assertThrows(ArithmeticException.class, () -> {
        var ignored = (int) wchar.invokeExact(0x1_0000);
      });
    }

    try (var arena = Arena.ofConfined()) {
      var sLongSegment = arena.allocate(LONG);
      var sLongVarHandle = LONG.varHandle();
      var sLongGetter = SLONG.adaptGetter(sLongVarHandle);
      var sLongSetter = SLONG.adaptSetter(sLongVarHandle);
      assertEquals(MethodType.methodType(long.class,
          MemorySegment.class, long.class), sLongGetter.type());
      assertEquals(MethodType.methodType(void.class,
          MemorySegment.class, long.class, long.class), sLongSetter.type());
      sLongSetter.invokeExact(sLongSegment, 0L, -321L);
      assertEquals(-321L,
          (long) sLongGetter.invokeExact(sLongSegment, 0L));

      var uLongSegment = arena.allocate(LONG);
      var uLongVarHandle = LONG.varHandle();
      var uLongGetter = ULONG.adaptGetter(uLongVarHandle);
      var uLongSetter = ULONG.adaptSetter(uLongVarHandle);
      assertEquals(MethodType.methodType(long.class,
          MemorySegment.class, long.class), uLongGetter.type());
      assertEquals(MethodType.methodType(void.class,
          MemorySegment.class, long.class, long.class), uLongSetter.type());
      uLongSetter.invokeExact(uLongSegment, 0L, 0xffff_ffffL);
      assertEquals(0xffff_ffffL,
          (long) uLongGetter.invokeExact(uLongSegment, 0L));

      var wcharSegment = arena.allocate(CanonicalLayout.WCHAR_T);
      var wcharVarHandle = CanonicalLayout.WCHAR_T.varHandle();
      var wcharGetter = NativeType.WCHAR.adaptGetter(wcharVarHandle);
      var wcharSetter = NativeType.WCHAR.adaptSetter(wcharVarHandle);
      assertEquals(MethodType.methodType(int.class,
          MemorySegment.class, long.class), wcharGetter.type());
      assertEquals(MethodType.methodType(void.class,
          MemorySegment.class, long.class, int.class), wcharSetter.type());
      wcharSetter.invokeExact(wcharSegment, 0L, 1234);
      assertEquals(1234, (int) wcharGetter.invokeExact(wcharSegment, 0L));
    }
  }
}

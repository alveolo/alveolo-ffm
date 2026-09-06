package org.alveolo.ffm;

import static java.lang.invoke.MethodHandles.identity;
import static org.alveolo.ffm.NativeType.SIZE_T;
import static org.alveolo.ffm.NativeType.SLONG;
import static org.alveolo.ffm.NativeType.ULONG;
import static org.alveolo.ffm.NativeType.WCHAR_T;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodType;

import org.junit.jupiter.api.Test;

class NativeTypeTest {
  @Test
  void adaptsSizeTCallsAndMemoryAccess() throws Throwable {
    var layout = SIZE_T.layout;
    var raw = identity(layout.carrier());
    var call = NativeType.adaptDowncall(raw, SIZE_T, SIZE_T);
    assertEquals(MethodType.methodType(long.class, long.class), call.type());
    if (layout.carrier() == long.class) {
      assertSame(raw, call);
    }

    var getter = SIZE_T.adaptGetter(layout.varHandle());
    var setter = SIZE_T.adaptSetter(layout.varHandle());
    assertEquals(MethodType.methodType(long.class,
        MemorySegment.class, long.class), getter.type());
    assertEquals(MethodType.methodType(void.class,
        MemorySegment.class, long.class, long.class), setter.type());

    var values = layout.carrier() == long.class
        ? new long[] {0, 0x8000_0000L, 0xffff_ffffL,
          0x1_0000_0000L, Long.MAX_VALUE, Long.MIN_VALUE, -1}
        : new long[] {0, 0x8000_0000L, 0xffff_ffffL};
    try (var arena = Arena.ofConfined()) {
      var segment = arena.allocate(layout, 2);
      var offset = layout.byteSize();
      for (var value : values) {
        assertEquals(value, (long) call.invokeExact(value));
        setter.invokeExact(segment, offset, value);
        assertEquals(value, NativeType.getSizeT(segment, offset));
        NativeType.setSizeT(segment, 0L, value);
        assertEquals(value, (long) getter.invokeExact(segment, 0L));
      }
      if (layout.carrier() == int.class) {
        for (var value : new long[] {-1, 0x1_0000_0000L}) {
          assertThrows(ArithmeticException.class, () -> {
            @SuppressWarnings("unused")
            var ignored = (long) call.invokeExact(value);
          });
          assertThrows(ArithmeticException.class, () -> {
            setter.invokeExact(segment, offset, value);
          });
          assertThrows(ArithmeticException.class,
              () -> NativeType.setSizeT(segment, 0L, value));
        }
      }
    }
  }

  @Test
  void adaptsNativeScalarHandleCarriers() throws Throwable {
    var sLongRaw = identity(SLONG.layout.carrier());
    var sLong = NativeType.adaptDowncall(sLongRaw, SLONG, SLONG);
    assertEquals(MethodType.methodType(long.class, long.class), sLong.type());
    assertEquals(-123L, (long) sLong.invokeExact(-123L));
    if (SLONG.layout.carrier() == long.class) {
      assertSame(sLongRaw, sLong);
    } else {
      assertThrows(ArithmeticException.class, () -> {
        @SuppressWarnings("unused")
        var ignored = (long) sLong.invokeExact(0x8000_0000L);
      });
    }

    var uLongRaw = identity(ULONG.layout.carrier());
    var uLong = NativeType.adaptDowncall(uLongRaw, ULONG, ULONG);
    assertEquals(MethodType.methodType(long.class, long.class), uLong.type());
    assertEquals(0xffff_ffffL, (long) uLong.invokeExact(0xffff_ffffL));
    if (ULONG.layout.carrier() == long.class) {
      assertSame(uLongRaw, uLong);
    } else {
      assertThrows(ArithmeticException.class, () -> {
        @SuppressWarnings("unused")
        var ignored = (long) uLong.invokeExact(-1L);
      });
      assertThrows(ArithmeticException.class, () -> {
        @SuppressWarnings("unused")
        var ignored = (long) uLong.invokeExact(0x1_0000_0000L);
      });
    }

    assertEquals(-1, NativeType.longToUnsignedIntExact(0xffff_ffffL));
    assertThrows(ArithmeticException.class,
        () -> NativeType.longToUnsignedIntExact(-1L));
    assertThrows(ArithmeticException.class,
        () -> NativeType.longToUnsignedIntExact(0x1_0000_0000L));

    var wcharRaw = identity(WCHAR_T.layout.carrier());
    var wchar = NativeType.adaptDowncall(wcharRaw, WCHAR_T, WCHAR_T);
    assertEquals(MethodType.methodType(int.class, int.class), wchar.type());
    assertEquals(0xffff, (int) wchar.invokeExact(0xffff));
    if (WCHAR_T.layout.carrier() == int.class) {
      assertSame(wcharRaw, wchar);
    } else {
      assertThrows(ArithmeticException.class, () -> {
        @SuppressWarnings("unused")
        var ignored = (int) wchar.invokeExact(0x1_0000);
      });
    }

    try (var arena = Arena.ofConfined()) {
      var sLongSegment = arena.allocate(SLONG.layout);
      var sLongVarHandle = SLONG.layout.varHandle();
      var sLongGetter = SLONG.adaptGetter(sLongVarHandle);
      var sLongSetter = SLONG.adaptSetter(sLongVarHandle);
      assertEquals(MethodType.methodType(long.class,
          MemorySegment.class, long.class), sLongGetter.type());
      assertEquals(MethodType.methodType(void.class,
          MemorySegment.class, long.class, long.class), sLongSetter.type());
      sLongSetter.invokeExact(sLongSegment, 0L, -321L);
      assertEquals(-321L,
          (long) sLongGetter.invokeExact(sLongSegment, 0L));

      var uLongSegment = arena.allocate(ULONG.layout);
      var uLongVarHandle = ULONG.layout.varHandle();
      var uLongGetter = ULONG.adaptGetter(uLongVarHandle);
      var uLongSetter = ULONG.adaptSetter(uLongVarHandle);
      assertEquals(MethodType.methodType(long.class,
          MemorySegment.class, long.class), uLongGetter.type());
      assertEquals(MethodType.methodType(void.class,
          MemorySegment.class, long.class, long.class), uLongSetter.type());
      uLongSetter.invokeExact(uLongSegment, 0L, 0xffff_ffffL);
      assertEquals(0xffff_ffffL,
          (long) uLongGetter.invokeExact(uLongSegment, 0L));

      var wcharSegment = arena.allocate(WCHAR_T.layout);
      var wcharVarHandle = WCHAR_T.layout.varHandle();
      var wcharGetter = WCHAR_T.adaptGetter(wcharVarHandle);
      var wcharSetter = WCHAR_T.adaptSetter(wcharVarHandle);
      assertEquals(MethodType.methodType(int.class,
          MemorySegment.class, long.class), wcharGetter.type());
      assertEquals(MethodType.methodType(void.class,
          MemorySegment.class, long.class, int.class), wcharSetter.type());
      wcharSetter.invokeExact(wcharSegment, 0L, 1234);
      assertEquals(1234, (int) wcharGetter.invokeExact(wcharSegment, 0L));
    }
  }
}

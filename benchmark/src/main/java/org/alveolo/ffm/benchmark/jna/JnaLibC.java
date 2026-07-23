package org.alveolo.ffm.benchmark.jna;

import static com.sun.jna.Platform.isWindows;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;

public interface JnaLibC extends Library {
  public static final JnaLibC INSTANCE = Native.load(
      isWindows() ? "ucrtbase" : Platform.C_LIBRARY_NAME, JnaLibC.class);

  int abs(int value);

  IntV abs(IntV value);

  NativeLong labs(NativeLong value);

  CLongV labs(CLongV value);

  long llabs(long value);

  LongV llabs(LongV value);

  div_t div(int numerator, int denominator);

  ldiv_t ldiv(NativeLong numerator, NativeLong denominator);

  lldiv_t lldiv(long numerator, long denominator);

  long strlen(String str);
}

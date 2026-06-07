package org.alveolo.ffm.benchmark.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface JnaLibC extends Library {
  public static final JnaLibC INSTANCE = Native.load("c", JnaLibC.class);

  int abs(int value);

  div_t div(int numerator, int denominator);

  ldiv_t ldiv(long numerator, long denominator);

  long strlen(String str);
}

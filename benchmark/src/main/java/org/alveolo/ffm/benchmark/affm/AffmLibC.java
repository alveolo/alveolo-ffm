package org.alveolo.ffm.benchmark.affm;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.ForeignName;

@ForeignInterface
public interface AffmLibC {
  int abs(int number);

  IntWrapper abs(IntWrapper number);

  @ForeignName("div")
  div_t_R div_r(int numerator, int denominator);

  @ForeignName("ldiv")
  ldiv_t_R ldiv_r(long numerator, long denominator);

  @ForeignName("div")
  div_t_S div_s(SegmentAllocator allocator, int numerator, int denominator);

  long strlen(String str);

  // String l64a(long n);
}

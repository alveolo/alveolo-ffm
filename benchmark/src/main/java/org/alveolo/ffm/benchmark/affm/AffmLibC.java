package org.alveolo.ffm.benchmark.affm;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.CLong;
import org.alveolo.ffm.SizeT;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Value;

@ForeignInterface
public interface AffmLibC {
  int abs(int number);

  IntWrapper abs(IntWrapper number);

  @Symbol("div")
  div_t_R div_r(int numerator, int denominator);

  @Symbol("ldiv")
  ldiv_t_R ldiv_r(
      @CLong long numerator, @CLong long denominator);

  @Symbol("div")
  @Value
  div_t_S div_s(SegmentAllocator allocator, int numerator, int denominator);

  @SizeT long strlen(String str);

  // String l64a(long n);
}

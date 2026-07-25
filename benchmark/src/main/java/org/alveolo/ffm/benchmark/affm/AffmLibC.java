package org.alveolo.ffm.benchmark.affm;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.SLong;
import org.alveolo.ffm.SizeT;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Value;

@ForeignInterface
public interface AffmLibC {
  int abs(int number);

  IntR abs(IntR number);

  @Value
  IntS abs(SegmentAllocator allocator, @Value IntS number);

  @SLong
  long labs(@SLong long number);

  CLongR labs(CLongR number);

  @Value
  CLongS labs(SegmentAllocator allocator, @Value CLongS number);

  long llabs(long number);

  LongR llabs(LongR number);

  @Value
  LongS llabs(SegmentAllocator allocator, @Value LongS number);

  @Symbol("div")
  div_t_R div_r(int numerator, int denominator);

  @Symbol("ldiv")
  ldiv_t_R ldiv_r(
      @SLong long numerator, @SLong long denominator);

  @Symbol("lldiv")
  lldiv_t_R lldiv_r(long numerator, long denominator);

  @Symbol("div")
  @Value
  div_t_S div_s(SegmentAllocator allocator, int numerator, int denominator);

  @Symbol("ldiv")
  @Value
  ldiv_t_S ldiv_s(
      SegmentAllocator allocator,
      @SLong long numerator, @SLong long denominator);

  @Symbol("lldiv")
  @Value
  lldiv_t_S lldiv_s(
      SegmentAllocator allocator, long numerator, long denominator);

  @SizeT
  long strlen(String str);

  // String l64a(long n);
}

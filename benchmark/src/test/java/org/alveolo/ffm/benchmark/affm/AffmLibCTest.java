package org.alveolo.ffm.benchmark.affm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.foreign.Arena;

import org.junit.jupiter.api.Test;

class AffmLibCTest {
  private static final AffmLibC affm = AffmLibCFFM.INSTANCE$F;

  @Test
  void primitives() {
    assertEquals(1, affm.abs(-1));
    assertEquals(43L, affm.labs(-43L));
    assertEquals((1L << 40) + 43, affm.llabs(-(1L << 40) - 43));
  }

  @Test
  void singleValueRecords() {
    assertEquals(1, affm.abs(new IntR(-1)).value());
    assertEquals(43L, affm.labs(new CLongR(-43L)).value());
    assertEquals((1L << 40) + 43,
        affm.llabs(new LongR(-(1L << 40) - 43)).value());
  }

  @Test
  void singleValueInterfaces() {
    try (var arena = Arena.ofConfined()) {
      var i = new IntSFM(arena).value(-1);
      var cLong = new CLongSFM(arena).value(-43L);
      var longLong = new LongSFM(arena).value(-(1L << 40) - 43);

      assertEquals(1, affm.abs(arena, i).value());
      assertEquals(43L, affm.labs(arena, cLong).value());
      assertEquals((1L << 40) + 43,
          affm.llabs(arena, longLong).value());
    }
  }

  @Test
  void returnsRecord() {
    var div = affm.div_r(7, 3);
    assertEquals(2, div.quot());
    assertEquals(1, div.rem());

    var ldiv = affm.ldiv_r(7L, 3L);
    assertEquals(2L, ldiv.quot());
    assertEquals(1L, ldiv.rem());

    var numerator = -(1L << 40) - 43;
    var lldiv = affm.lldiv_r(numerator, 5L);
    assertEquals(numerator / 5L, lldiv.quot());
    assertEquals(numerator % 5L, lldiv.rem());
  }

  @Test
  void returnsInterface() {
    try (var arena = Arena.ofConfined()) {
      var div = affm.div_s(arena, 7, 3);
      assertEquals(2, div.quot());
      assertEquals(1, div.rem());

      var ldiv = affm.ldiv_s(arena, 7L, 3L);
      assertEquals(2L, ldiv.quot());
      assertEquals(1L, ldiv.rem());

      var numerator = -(1L << 40) - 43;
      var lldiv = affm.lldiv_s(arena, numerator, 5L);
      assertEquals(numerator / 5L, lldiv.quot());
      assertEquals(numerator % 5L, lldiv.rem());
    }
  }

  @Test
  void utf8zStringParam() {
    assertEquals(0L, affm.strlen(""));
    assertEquals(6L, affm.strlen("ASCIIZ"));
    assertEquals(12L, affm.strlen("Юникод"));
  }

  @Test
  void alignment() {
    assertEquals(0L, AlignmentFM.MemoryLayout$F.byteOffset(
        AlignmentFM.b$PathElement$F));
    assertEquals(4L, AlignmentFM.MemoryLayout$F.byteOffset(
        AlignmentFM.i$PathElement$F));
    assertEquals(8L, AlignmentFM.MemoryLayout$F.byteOffset(
        AlignmentFM.x$PathElement$F));
    assertEquals(10L, AlignmentFM.MemoryLayout$F.byteOffset(
        AlignmentFM.c$PathElement$F));
    assertEquals(16L, AlignmentFM.MemoryLayout$F.byteOffset(
        AlignmentFM.l$PathElement$F));
  }

  // @Test
  // void utf8zStringReturn() {
  // assertEquals("TODO", affm.l64a(0L));
  // assertEquals("TODO", affm.l64a(0xFAFAFAFAFAFAFAFAL));
  // }
}

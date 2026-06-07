package org.alveolo.ffm.benchmark.affm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.foreign.Arena;

import org.junit.jupiter.api.Test;

class AffmLibCTest {
  private static final AffmLibC affm = AffmLibCFFM.INSTANCE;

  @Test
  void primitives() {
    assertEquals(1, affm.abs(-1));
    assertEquals(1, affm.abs(new IntWrapper(-1)).value());
  }

  @Test
  void returnsRecord() {
    var div = affm.div_r(7, 3);
    assertEquals(2, div.quot());
    assertEquals(1, div.rem());

    var ldiv = affm.ldiv_r(7L, 3L);
    assertEquals(2L, ldiv.quot());
    assertEquals(1L, ldiv.rem());
  }

  @Test
  void returnsInterface() {
    try (var arena = Arena.ofConfined()) {
      var div = affm.div_s(arena, 7, 3);
      assertEquals(2, div.quot());
      assertEquals(1, div.rem());
    }

    // var ldiv = affm.ldiv_s(7L, 3L);
    // assertEquals(2L, ldiv.quot());
    // assertEquals(1L, ldiv.rem());
  }

  @Test
  void utf8zStringParam() {
    assertEquals(0L, affm.strlen(""));
    assertEquals(6L, affm.strlen("ASCIIZ"));
    assertEquals(12L, affm.strlen("Юникод"));
  }

  @Test
  void alignment() {
    assertEquals(0L, AlignmentFM.FM$LAYOUT.byteOffset(AlignmentFM.FM$PE$b));
    assertEquals(4L, AlignmentFM.FM$LAYOUT.byteOffset(AlignmentFM.FM$PE$i));
    assertEquals(8L, AlignmentFM.FM$LAYOUT.byteOffset(AlignmentFM.FM$PE$x));
    assertEquals(10L, AlignmentFM.FM$LAYOUT.byteOffset(AlignmentFM.FM$PE$c));
    assertEquals(16L, AlignmentFM.FM$LAYOUT.byteOffset(AlignmentFM.FM$PE$l));
  }

  // @Test
  // void utf8zStringReturn() {
  // assertEquals("TODO", affm.l64a(0L));
  // assertEquals("TODO", affm.l64a(0xFAFAFAFAFAFAFAFAL));
  // }
}

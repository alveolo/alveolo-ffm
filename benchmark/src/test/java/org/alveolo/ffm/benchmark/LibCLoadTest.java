package org.alveolo.ffm.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.alveolo.ffm.benchmark.jna.CLongV;
import org.alveolo.ffm.benchmark.jna.IntV;
import org.alveolo.ffm.benchmark.jna.JnaLibC;
import org.alveolo.ffm.benchmark.jna.LongV;
import org.alveolo.ffm.benchmark.jnr.JnrLibC;
import org.junit.jupiter.api.Test;

import com.sun.jna.NativeLong;

class LibCLoadTest {
  @Test
  void loadsJnaLibC() {
    assertEquals(42, JnaLibC.INSTANCE.abs(-42));
    assertEquals(42, JnaLibC.INSTANCE.abs(new IntV(-42)).value);
    assertEquals(43L,
        JnaLibC.INSTANCE.labs(new NativeLong(-43)).longValue());
    assertEquals(43L,
        JnaLibC.INSTANCE.labs(new CLongV(-43)).value.longValue());
    assertEquals((1L << 40) + 43,
        JnaLibC.INSTANCE.llabs(-(1L << 40) - 43));
    assertEquals((1L << 40) + 43,
        JnaLibC.INSTANCE.llabs(
            new LongV(-(1L << 40) - 43)).value);

    var div = JnaLibC.INSTANCE.ldiv(
        new NativeLong(-43), new NativeLong(5));
    assertEquals(-8, div.quot.longValue());
    assertEquals(-3, div.rem.longValue());

    var numerator = -(1L << 40) - 43;
    var lldiv = JnaLibC.INSTANCE.lldiv(numerator, 5L);
    assertEquals(numerator / 5L, lldiv.quot);
    assertEquals(numerator % 5L, lldiv.rem);
  }

  @Test
  void loadsJnrLibC() {
    assertEquals(42, JnrLibC.INSTANCE.abs(-42));
    assertEquals(43L, JnrLibC.INSTANCE.labs(-43L));
    assertEquals((1L << 40) + 43,
        JnrLibC.INSTANCE.llabs(-(1L << 40) - 43));
  }
}

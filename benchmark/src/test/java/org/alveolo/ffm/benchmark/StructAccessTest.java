package org.alveolo.ffm.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.foreign.Arena;

import org.alveolo.ffm.benchmark.affm.AffmStruct;
import org.alveolo.ffm.benchmark.jna.JnaStruct;
import org.alveolo.ffm.benchmark.jnr.JnrStruct;
import org.junit.jupiter.api.Test;

import com.sun.jna.NativeLong;

import jnr.ffi.Memory;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;

class StructAccessTest {
  @Test
  void equivalentLayoutsAndFieldAccess() {
    var i = -42;
    var cLong = -43L;
    var l = -(1L << 40) - 44;

    try (var arena = Arena.ofConfined()) {
      var affm = new AffmStruct(arena).i(i).cLong(cLong).l(l);
      assertEquals(i, affm.i());
      assertEquals(cLong, affm.cLong());
      assertEquals(l, affm.l());

      var jna = new JnaStruct();
      jna.writeField("i", i);
      jna.writeField("cLong", new NativeLong(cLong));
      jna.writeField("l", l);
      assertEquals(i, jna.readField("i"));
      assertEquals(cLong, ((NativeLong) jna.readField("cLong")).longValue());
      assertEquals(l, jna.readField("l"));

      var runtime = Runtime.getSystemRuntime();
      var jnr = new JnrStruct(runtime);
      jnr.useMemory(Memory.allocateDirect(runtime, Struct.size(jnr)));
      jnr.i.set(i);
      jnr.cLong.set(cLong);
      jnr.l.set(l);
      assertEquals(i, jnr.i.get());
      assertEquals(cLong, jnr.cLong.get());
      assertEquals(l, jnr.l.get());

      assertEquals(AffmStruct.MemoryLayout$F.byteSize(), jna.size());
      assertEquals(AffmStruct.MemoryLayout$F.byteSize(), Struct.size(jnr));
    }
  }
}

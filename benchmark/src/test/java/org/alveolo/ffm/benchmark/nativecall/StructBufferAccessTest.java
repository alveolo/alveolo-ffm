package org.alveolo.ffm.benchmark.nativecall;

import static java.nio.ByteOrder.nativeOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.foreign.Arena;
import java.nio.IntBuffer;

import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;
import org.junit.jupiter.api.Test;

class StructBufferAccessTest {
  @Test
  void usesNativeByteOrderForTypedBufferView() {
    try (var arena = Arena.ofConfined()) {
      var struct = new NativeIntsFM(arena);

      struct.values(0, 0x01020304);

      assertEquals(nativeOrder(), struct.values().order());
      assertEquals(0x01020304, struct.values().get(0));

      struct.values().put(0, 0x11223344);

      assertEquals(0x11223344, struct.values(0));
    }
  }
}

@Struct
interface NativeInts {
  @Sequence(1)
  IntBuffer values();
}

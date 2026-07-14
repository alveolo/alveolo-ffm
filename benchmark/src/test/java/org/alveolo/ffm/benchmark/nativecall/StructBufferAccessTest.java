package org.alveolo.ffm.benchmark.nativecall;

import static java.nio.ByteOrder.nativeOrder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import org.alveolo.ffm.Address;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;
import org.junit.jupiter.api.Test;

class StructBufferAccessTest {
  @Test
  void usesNativeByteOrderForTypedBufferView() {
    try (var arena = Arena.ofConfined()) {
      var struct = new NativeIntsFM(arena);

      struct.valuesFromArray$F(new int[] {0x01020304, 2, 3});

      assertEquals(nativeOrder(), struct.valuesAsBuffer$F().order());
      assertEquals(0x01020304, struct.valuesAsBuffer$F().get(0));
      assertArrayEquals(
          new int[] {0x01020304, 2, 3}, struct.valuesToArray$F());

      struct.valuesAsBuffer$F().put(0, 0x11223344);

      assertEquals(0x11223344, struct.values(0));
      assertThrows(IllegalArgumentException.class,
          () -> struct.valuesFromArray$F(new int[2]));
    }
  }

  @Test
  void accessesMatricesInlineRecordsAndPointerSlots() {
    try (var arena = Arena.ofConfined()) {
      var struct = new NativeArraysFM(arena)
          .matrix(1, 2, 42)
          .points(0, new PairR(1, 2))
          .pointersAsAddress$F(0, MemorySegment.NULL)
          .pointers(arena, 1, new PairR(7, 8));

      assertEquals(42, struct.matrix(1, 2));
      assertEquals(new PairR(1, 2), struct.points(0));
      assertNull(struct.pointers(0));
      assertEquals(new PairR(7, 8), struct.pointers(1));
      assertEquals(Integer.BYTES,
          struct.matrixAsMemorySegment$F(1, 2).byteSize());
      assertThrows(IndexOutOfBoundsException.class,
          () -> struct.matrix(2, 0));
    }
  }

  @Test
  void snapshotsRecordArraysAndAccessesStandaloneStructArrays() {
    try (var arena = Arena.ofConfined()) {
      var source = new NativeHeader(
          new byte[] {1, 2, 3},
          new PairR[] {new PairR(4, 5), new PairR(6, 7)});
      var segment = NativeHeaderFM.toMemorySegment$F(arena, source);
      var snapshot = NativeHeaderFM.fromMemorySegment$F(segment);

      assertArrayEquals(source.id(), snapshot.id());
      assertArrayEquals(source.points(), snapshot.points());

      source.id()[0] = 99;
      snapshot.points()[0] = new PairR(99, 99);
      var reread = NativeHeaderFM.fromMemorySegment$F(segment);
      assertArrayEquals(new byte[] {1, 2, 3}, reread.id());
      assertArrayEquals(
          new PairR[] {new PairR(4, 5), new PairR(6, 7)},
          reread.points());

      assertThrows(IllegalArgumentException.class,
          () -> NativeHeaderFM.toMemorySegment$F(arena,
              new NativeHeader(new byte[2], source.points())));

      var pairs = PairRFM.allocate$F(arena, 2);
      var pairSize = PairRFM.MemoryLayout$F.byteSize();
      PairRFM.toMemorySegment$F(
          new PairR(10, 20), pairs.asSlice(0, pairSize));
      PairRFM.toMemorySegment$F(
          new PairR(30, 40), pairs.asSlice(pairSize, pairSize));
      assertEquals(new PairR(10, 20), PairRFM.at$F(pairs, 0));
      assertEquals(new PairR(30, 40), PairRFM.at$F(pairs, 1));
      assertThrows(IndexOutOfBoundsException.class,
          () -> PairRFM.at$F(pairs, 2));
    }
  }
}

@Struct
interface NativeInts {
  int values(@Sequence(3) int index);
}

@Struct
interface NativeArrays {
  int matrix(
      @Sequence(2) int row,
      @Sequence(3) long column);

  @Value
  PairR points(@Sequence(2) long index);

  @Address
  PairR pointers(@Sequence(2) long index);
}

@Struct
record NativeHeader(
    @Sequence(3) byte[] id,
    @Sequence(2) PairR[] points) {}

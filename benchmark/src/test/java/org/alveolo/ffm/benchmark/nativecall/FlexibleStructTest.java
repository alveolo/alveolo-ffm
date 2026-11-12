package org.alveolo.ffm.benchmark.nativecall;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

import org.alveolo.ffm.Flexible;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.SizeT;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;
import org.junit.jupiter.api.Test;

class FlexibleStructTest {
  @Test
  void boundedViewUsesTheHeaderCountOnceAndKeepsTheCallerBounds() {
    try (var arena = Arena.ofConfined()) {
      var rows = new FlexibleRowsFM(arena, (byte) 3).rows(2, 9, (byte) 42);
      var bytes = rows.MemorySegment$F;
      assertEquals(9, rows.rows$MemorySegment$F.address() - bytes.address());
      assertEquals(39, bytes.byteSize());
      assertEquals(3, bytes.get(ValueLayout.JAVA_BYTE, 8));
      assertEquals(42, bytes.get(ValueLayout.JAVA_BYTE, 38));
      FlexibleRowsFM.rows$VarHandle$F.set(rows.rows$MemorySegment$F, 1L, 2L, (byte) 17);
      assertEquals(17, rows.rows(1, 2));
      var readOnly = new FlexibleRowsFM(bytes.asReadOnly());
      assertEquals(30, readOnly.rows$MemorySegment$F.byteSize());
      assertThrows(IllegalArgumentException.class, () -> readOnly.rows(2, 9, (byte) 1));
      assertThrows(IndexOutOfBoundsException.class, () -> readOnly.rows(3, 0));
      assertThrows(IndexOutOfBoundsException.class, () -> readOnly.rows(0, 10));
      assertThrows(IndexOutOfBoundsException.class,
          () -> new FlexibleRowsFM(bytes.asSlice(0, bytes.byteSize() - 1)));
      FlexibleRowsFM.count$VarHandle$F.set(bytes, (byte) 1);
      assertEquals(1, readOnly.count());
      assertEquals(10, new FlexibleRowsFM(bytes).rows$MemorySegment$F.byteSize());
      assertEquals(42, readOnly.rows(2, 9));
    }
  }

  @Test
  void wrapsReadOnlyStorageWithAnExplicitCapacity() {
    try (var arena = Arena.ofConfined()) {
      var bytes = arena.allocate(64, FlexibleRowsFM.MemoryLayout$F.byteAlignment());
      FlexibleRowsFM.allocateFlexible$F(
          SegmentAllocator.prefixAllocator(bytes), (byte) 1);
      var readOnly = bytes.asReadOnly();
      var rows = new FlexibleRowsFM(readOnly, (byte) 2);
      assertSame(readOnly, rows.MemorySegment$F);
      assertEquals(1, rows.count());
      assertEquals(20, rows.rows$MemorySegment$F.byteSize());
      assertThrows(IndexOutOfBoundsException.class,
          () -> new FlexibleRowsFM(bytes, (byte) 6));
      assertThrows(IndexOutOfBoundsException.class, () -> rows.rows(2, 0));
    }
  }

  @Test
  void handlesZeroLengthSignedCountsAndAllocationOverflow() {
    try (var arena = Arena.ofConfined()) {
      var empty = new FlexibleRowsFM(arena, (byte) 0);
      assertEquals(FlexibleRowsFM.MemoryLayout$F.byteSize(), empty.MemorySegment$F.byteSize());
      assertEquals(0, empty.rows$MemorySegment$F.byteSize());
      assertThrows(IndexOutOfBoundsException.class, () -> empty.rows(0, 0));
      var large = new FlexibleRowsFM(arena, Byte.MAX_VALUE);
      assertEquals(10L * Byte.MAX_VALUE,
          new FlexibleRowsFM(large.MemorySegment$F).rows$MemorySegment$F.byteSize());
      assertThrows(IllegalArgumentException.class,
          () -> new FlexibleRowsFM(arena, (byte) -1));
      assertThrows(IllegalArgumentException.class, () -> new FlexiblePacketsFM(arena, -1));
      assertThrows(IllegalArgumentException.class,
          () -> FlexiblePacketsFM.reinterpretFlexible$F(
              MemorySegment.NULL, -1));
      assertThrows(ArithmeticException.class, () -> new FlexiblePacketsFM(arena, Long.MAX_VALUE));
      assertNull(FlexibleRowsFM.reinterpret$F(MemorySegment.NULL));
      assertNull(FlexiblePacketsFM.reinterpret$F(MemorySegment.NULL, 0));
      assertNull(FlexiblePacketsFM.reinterpretFlexible$F(
          MemorySegment.NULL, 3));
    }
  }

  @Test
  void rejectsNegativeNativeCountsBeforeExpandingAnAddress() {
    try (var arena = Arena.ofConfined()) {
      var bytes = arena.allocate(SignedRowsFM.MemoryLayout$F);
      bytes.set(ValueLayout.JAVA_INT, Long.BYTES, -1);
      assertThrows(IllegalArgumentException.class, () -> new SignedRowsFM(bytes));
      var address = MemorySegment.ofAddress(bytes.address());
      assertThrows(IllegalArgumentException.class, () -> SignedRowsFM.reinterpret$F(address));
    }
  }

  @Test
  void preservesTheUnsignedRangeOfACharCount() {
    try (var arena = Arena.ofConfined()) {
      var chars = new CharCountFM(arena, Character.MAX_VALUE);
      assertEquals(Character.MAX_VALUE, chars.count());
      var alias = CharCountFM.reinterpret$F(chars.MemorySegment$F.asReadOnly());
      assertEquals(65535, alias.data$MemorySegment$F.byteSize());
    }
  }

  @Test
  void excludesPaddingEvenWhenItCouldHoldAnotherElement() {
    try (var arena = Arena.ofConfined()) {
      var one = new PaddedBytesFM(arena, 1).data(0, (byte) 7);
      assertEquals(PaddedBytesFM.MemoryLayout$F.byteSize(), one.MemorySegment$F.byteSize());
      assertArrayEquals(new byte[] {7}, one.dataToArray$F());
      var tail = one.data$MemorySegment$F;
      PaddedBytesFM.data$VarHandle$F.set(tail, 0L, (byte) 9);
      assertEquals(9, one.data(0));
      assertThrows(IndexOutOfBoundsException.class,
          () -> PaddedBytesFM.data$VarHandle$F.set(tail, 1L, (byte) 8));
      assertThrows(IndexOutOfBoundsException.class, () -> one.data(1, (byte) 8));
      var empty = new PaddedBytesFM(one.MemorySegment$F, 0);
      assertEquals(one.MemorySegment$F.byteSize(), empty.MemorySegment$F.byteSize());
      assertEquals(0, empty.data$MemorySegment$F.byteSize());
      assertThrows(IndexOutOfBoundsException.class, () -> empty.data(0));
      var extent = PaddedBytesFM.reinterpret$F(
          MemorySegment.ofAddress(one.MemorySegment$F.address()),
          one.MemorySegment$F.byteSize());
      assertEquals(one.MemorySegment$F.byteSize()
              - PaddedBytesFM.MemoryLayout$F.byteOffset(
                  PaddedBytesFM.data$PathElement$F),
          extent.data$MemorySegment$F.byteSize());
      var shortView = new PaddedBytesFM(one.MemorySegment$F.asSlice(1, 10), 1);
      shortView.data(0, (byte) 11);
      assertEquals(11, one.MemorySegment$F.get(ValueLayout.JAVA_BYTE, 10));
    }
  }

  @Test
  void wrapsAnExplicitNumberOfInlineStructsAndPreservesTheHeader() {
    try (var arena = Arena.ofConfined()) {
      var request = new FlexiblePacketsFM(arena, 2).streamId(42);
      request.packets(1).left(19);
      var address = MemorySegment.ofAddress(request.MemorySegment$F.address());
      var alias = FlexiblePacketsFM.reinterpretFlexible$F(address, 2);
      assertEquals(42, alias.streamId());
      assertEquals(19, alias.packets(1).left());
      alias.packets(1).right(27);
      assertEquals(27, request.packets(1).right());
      var empty = new FlexiblePacketsFM(request.MemorySegment$F, 0);
      assertEquals(42, empty.streamId());
      assertThrows(IndexOutOfBoundsException.class, () -> empty.packets(0));
      assertThrows(IndexOutOfBoundsException.class, () -> alias.packets(-1));
      assertThrows(ArithmeticException.class, () -> alias.packets(1L << 61));
      assertThrows(IndexOutOfBoundsException.class, () -> new FlexiblePacketsFM(request.MemorySegment$F, 3));

      var sized = FlexiblePacketsFM.reinterpret$F(
          address, request.MemorySegment$F.byteSize());
      assertEquals(request.MemorySegment$F.byteSize(),
          sized.MemorySegment$F.byteSize());
      assertEquals(2 * PairSFM.MemoryLayout$F.byteSize(),
          sized.packets$MemorySegment$F.byteSize());
      assertEquals(27, sized.packets(1).right());
    }
  }

  @Test
  void exposesOnlyTheDeclaredElementsThroughArrayAndBufferViews() {
    try (var arena = Arena.ofConfined()) {
      var samples = new FlexibleSamplesFM(arena, 3).valuesFromArray$F(new int[] {1, 2, 3});
      var alias = FlexibleSamplesFM.reinterpret$F(samples.MemorySegment$F);
      assertEquals(3, alias.size());
      alias.valuesAsBuffer$F().put(2, 19);
      assertArrayEquals(new int[] {1, 2, 19}, samples.valuesToArray$F());
      assertThrows(IllegalArgumentException.class, () -> alias.valuesFromArray$F(new int[4]));
      var shorter = new FlexibleSamplesFM(samples.MemorySegment$F, 1);
      assertEquals(3, alias.size());
      assertArrayEquals(new int[] {1}, shorter.valuesToArray$F());
    }
  }

  @Test
  void resolvesPointerFieldsAndBoundsEveryFixedInnerDimension() {
    try (var arena = Arena.ofConfined()) {
      var row = new SignedRowsFM(arena, 1).data(0, (byte) 76);
      var table = new FlexiblePointersFM(arena, 2).pointers(1, 1, row);
      assertEquals(row.MemorySegment$F.address(),
          table.pointersAsAddress$F(1, 1).address());
      assertEquals(76, table.pointers(1, 1).data(0));
      assertNull(table.pointers(0, 1));
      assertThrows(IndexOutOfBoundsException.class, () -> table.pointersAsAddress$F(2, 0));
      assertThrows(IndexOutOfBoundsException.class, () -> table.pointers(1, 2, row));
      var firstRow = new FlexiblePointersFM(table.MemorySegment$F, 1);
      assertThrows(IndexOutOfBoundsException.class,
          () -> firstRow.pointersAsAddress$F(1, 0, row.MemorySegment$F));
    }
  }
}

@Struct
interface FlexibleRows {
  long tag();
  byte count();
  byte rows(@Flexible("count") long row, @Sequence(10) int column);
}

@Struct
interface SignedRows {
  long tag();
  int count();
  byte flags();
  byte data(@Flexible("count") long i);
}

@Struct
interface CharCount {
  char count();
  byte data(@Flexible("count") int i);
}

@Struct
interface FlexiblePackets {
  int streamId();
  @Value PairSFM packets(@Flexible long index);
}

@Struct
interface FlexibleSamples {
  @SizeT long size();
  int values(@Flexible("size") int index);
}

@Struct
interface FlexiblePointers {
  int tag();
  SignedRowsFM pointers(@Flexible long row, @Sequence(2) int column);
}

@Struct
interface PaddedBytes {
  long tag();
  byte flags();
  byte data(@Flexible long index);
}

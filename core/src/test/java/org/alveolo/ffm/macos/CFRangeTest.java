package org.alveolo.ffm.macos;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.junit.jupiter.api.Test;

class CFRangeTest {
  @Test
  void readsAndWritesBackingMemory() {
    var words = new long[] {7, 11};
    var range = new CFRange(MemorySegment.ofArray(words));

    assertEquals(16, CFRange.MemoryLayout$F.byteSize());
    assertEquals(8, CFRange.MemoryLayout$F.byteAlignment());
    assertEquals(0, CFRange.MemoryLayout$F.byteOffset(groupElement("location")));
    assertEquals(8, CFRange.MemoryLayout$F.byteOffset(groupElement("length")));
    assertEquals(7, range.location());
    assertEquals(11, range.length());
    assertSame(range, range.location(-1).length(23));
    assertArrayEquals(new long[] {-1, 23}, words);

    words[0] = 3;
    assertEquals(3, range.location());
  }

  @Test
  void usesCallerAllocationAndLifetime() {
    CFRange range;
    try (var arena = Arena.ofConfined()) {
      range = new CFRange(arena).location(5).length(13);
      assertSame(arena.scope(), range.MemorySegment$F.scope());
      assertEquals(16, range.MemorySegment$F.byteSize());
      assertEquals(5, range.location());
      assertEquals(13, range.length());
    }
    assertThrows(IllegalStateException.class, range::length);
  }
}

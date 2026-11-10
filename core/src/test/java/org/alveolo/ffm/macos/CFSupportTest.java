package org.alveolo.ffm.macos;

import static java.lang.foreign.MemorySegment.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.foreign.MemorySegment;

import org.junit.jupiter.api.Test;

class CFSupportTest {
  @Test
  void failedAllocationOfANonNullStringThrowsOutOfMemoryError() {
    var cf = new CFSupport() {
      int attempts;

      @Override
      public MemorySegment CFStringCreateWithCharacters(MemorySegment allocator,
          MemorySegment characters, long length) {
        attempts++;
        return NULL;
      }

      @Override
      public long CFStringGetLength(MemorySegment value) {
        throw new AssertionError();
      }

      @Override
      public void CFStringGetCharacters(MemorySegment value, CFRange range,
          MemorySegment output) {
        throw new AssertionError();
      }

      @Override
      public void CFRelease(MemorySegment value) {
        throw new AssertionError();
      }
    };
    assertEquals(NULL, cf.toCFString(null));
    assertEquals(0, cf.attempts);
    assertThrows(OutOfMemoryError.class, () -> cf.toCFString(""));
    assertThrows(OutOfMemoryError.class, () -> cf.toCFString("key"));
    assertEquals(2, cf.attempts);
  }
}

package org.alveolo.ffm.benchmark.macos;

import static java.lang.foreign.ValueLayout.JAVA_CHAR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Locale;

import org.alveolo.ffm.macos.CFRange;
import org.alveolo.ffm.macos.CFSupport;
import org.junit.jupiter.api.Test;

class CoreFoundationTest {
  @Test
  void getsCFStringLengthFromJavaString() {
    assumeMac();

    var coreFoundation = CoreFoundationFFM.INSTANCE$F;

    assertEquals(5L, coreFoundation.CFStringGetLength("ASCII"));
    assertEquals(6L, coreFoundation.CFStringGetLength("Юникод"));
  }

  @Test
  void convertsOwnedCFStringReturnToJavaString() {
    assumeMac();

    var coreFoundation = CoreFoundationFFM.INSTANCE$F;

    assertEquals("ASCII!", coreFoundation.create(MemorySegment.NULL,
        "ASCII!", CFSupport.kCFStringEncodingUTF8));
    assertEquals("Юникод", coreFoundation.create(MemorySegment.NULL,
        "Юникод", CFSupport.kCFStringEncodingUTF8));
  }

  @Test
  void preservesUtf16CodeUnitsInBothDirections() {
    assumeMac();
    var coreFoundation = CoreFoundationFFM.INSTANCE$F;
    for (var value : new String[] {"", "\u0000", "A\u0000B", "\u0000start",
        "end\u0000", "Юникод", "A😀B", "A\uD800B", "A\uDC00B"}) {
      assertEquals(value.length(), coreFoundation.CFStringGetLength(value));
      var nativeValue = CoreFoundationFFM.INSTANCE$F.toCFString(value);
      try {
        assertNotEquals(0L, nativeValue.address());
        assertEquals(value, CoreFoundationFFM.INSTANCE$F.fromCFString(nativeValue));
      } finally {
        CoreFoundationFFM.INSTANCE$F.CFRelease(nativeValue);
      }
      assertEquals(value, coreFoundation.createCharacters(MemorySegment.NULL,
          value.toCharArray(), value.length()));
    }
  }

  @Test
  void convertsCFStringAlongsideSharedAllocations() {
    assumeMac();
    var value = "A\u0000Б😀";
    var expected = value.getBytes(UTF_8);
    var buffer = new byte[expected.length];
    var used = new long[1];

    try (var arena = Arena.ofConfined()) {
      var range = new CFRange(arena).location(0).length(value.length());
      var converted = CoreFoundationFFM.INSTANCE$F.CFStringGetBytes(value,
          range, CFSupport.kCFStringEncodingUTF8,
          (byte) 0, false, buffer, buffer.length, used);
      assertEquals(value.length(), converted);
    }
    assertEquals(expected.length, used[0]);
    assertArrayEquals(expected, buffer);
  }

  @Test
  void passesMemoryBackedRangesByValue() {
    assumeMac();
    var coreFoundation = CoreFoundationFFM.INSTANCE$F;
    var text = "A\u0000Б😀\uD800Z";
    var value = coreFoundation.toCFString(text);
    try (var arena = Arena.ofConfined()) {
      var range = new CFRange(arena);
      var characters = arena.allocate(JAVA_CHAR, text.length());
      for (int start = 0; start < text.length(); start++) {
        range.location(start).length(text.length() - start);
        coreFoundation.CFStringGetCharacters(value, range, characters);
        assertEquals(text.substring(start), new String(characters.asSlice(
            0, JAVA_CHAR.byteSize() * range.length()).toArray(JAVA_CHAR)));
        assertEquals(start, range.location());
        assertEquals(text.length() - start, range.length());
      }

      var heap = new CFRange(MemorySegment.ofArray(
          new long[] {1, 4}).asReadOnly());
      coreFoundation.CFStringGetCharacters(value, heap, characters);
      assertEquals(text.substring(1, 5), new String(characters.asSlice(
          0, JAVA_CHAR.byteSize() * heap.length()).toArray(JAVA_CHAR)));
    } finally {
      coreFoundation.CFRelease(value);
    }
  }

  @Test
  void overridesDefaultValueRangeWithAddressForSearchResult() {
    assumeMac();
    var coreFoundation = CoreFoundationFFM.INSTANCE$F;
    var text = "prefix Б😀 suffix";
    var search = new CFRange(MemorySegment.ofArray(
        new long[] {7, text.length() - 7}).asReadOnly());
    try (var arena = Arena.ofConfined()) {
      var result = new CFRange(arena).location(-1).length(0);
      assertTrue(coreFoundation.CFStringFindWithOptions(text, "Б😀", search, 0, result));
      assertEquals(7, result.location());
      assertEquals(3, result.length());
      assertEquals(7, search.location());
      assertEquals(text.length() - 7, search.length());
      assertTrue(coreFoundation.CFStringFindWithOptions(text, "Б😀", search, 0, null));
    }
  }

  @Test
  void preservesNullStrings() {
    assumeMac();
    assertEquals(MemorySegment.NULL, CoreFoundationFFM.INSTANCE$F.toCFString(null));
    assertNull(CoreFoundationFFM.INSTANCE$F.fromCFString(MemorySegment.NULL));
    assertNull(CoreFoundationFFM.INSTANCE$F.fromCFString(null));
  }

  private static void assumeMac() {
    assumeTrue(System.getProperty("os.name").toLowerCase(Locale.ROOT)
        .contains("mac"));
  }
}

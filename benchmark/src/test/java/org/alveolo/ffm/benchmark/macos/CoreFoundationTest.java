package org.alveolo.ffm.benchmark.macos;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.foreign.MemorySegment;
import java.util.Locale;

import org.alveolo.ffm.macos.CFStringSupport;
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
        "ASCII!", CFStringSupport.kCFStringEncodingUTF8));
    assertEquals("Юникод", coreFoundation.create(MemorySegment.NULL,
        "Юникод", CFStringSupport.kCFStringEncodingUTF8));
  }

  @Test
  void preservesUtf16CodeUnitsInBothDirections() {
    assumeMac();
    var coreFoundation = CoreFoundationFFM.INSTANCE$F;
    for (var value : new String[] {"", "\u0000", "A\u0000B", "\u0000start",
        "end\u0000", "Юникод", "A😀B", "A\uD800B", "A\uDC00B"}) {
      assertEquals(value.length(), coreFoundation.CFStringGetLength(value));
      var nativeValue = CFStringSupport.toCFString(value);
      try {
        assertNotEquals(0L, nativeValue.address());
        assertEquals(value, CFStringSupport.toJavaString(nativeValue));
      } finally {
        CFStringSupport.release(nativeValue);
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

    var converted = CoreFoundationFFM.INSTANCE$F.CFStringGetBytes(value,
        new CFRange(0, value.length()), CFStringSupport.kCFStringEncodingUTF8,
        (byte) 0, false, buffer, buffer.length, used);

    assertEquals(value.length(), converted);
    assertEquals(expected.length, used[0]);
    assertArrayEquals(expected, buffer);
  }

  @Test
  void preservesNullStrings() {
    assumeMac();
    assertEquals(MemorySegment.NULL, CFStringSupport.toCFString(null));
    assertNull(CFStringSupport.toJavaString(MemorySegment.NULL));
    assertNull(CFStringSupport.toJavaString(null));
    CFStringSupport.release(MemorySegment.NULL);
    CFStringSupport.release(null);
  }

  private static void assumeMac() {
    assumeTrue(System.getProperty("os.name").toLowerCase(Locale.ROOT)
        .contains("mac"));
  }
}

package org.alveolo.ffm.benchmark.macos;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

  private static void assumeMac() {
    assumeTrue(System.getProperty("os.name").toLowerCase(Locale.ROOT)
        .contains("mac"));
  }
}

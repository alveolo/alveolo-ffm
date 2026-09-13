package org.alveolo.ffm.macos;

import static java.lang.foreign.ValueLayout.JAVA_CHAR;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/// Native declarations and conversion helpers for CoreFoundation.
///
/// Extend this interface from an application [org.alveolo.ffm.ForeignInterface]
/// and configure CoreFoundation lookup on that binding. Inherited native
/// methods are generated there; default helpers use the same binding instance.
public interface CFSupport {
  int kCFStringEncodingUTF8 = 0x08000100;

  MemorySegment CFStringCreateWithCharacters(
      MemorySegment allocator, MemorySegment characters, long length);

  long CFStringGetLength(MemorySegment value);

  void CFStringGetCharacters(MemorySegment value,
      CFRange range, MemorySegment characters);

  void CFRelease(MemorySegment value);

  /// Converts a Java String to an owned native reference.
  /// Returns [MemorySegment#NULL] for null input, never Java null.
  /// Overrides must preserve this return contract.
  default MemorySegment toCFString(String value) {
    if (value == null) return MemorySegment.NULL;

    try (var arena = Arena.ofConfined()) {
      var characters = arena.allocateFrom(JAVA_CHAR, value.toCharArray());
      return CFStringCreateWithCharacters(MemorySegment.NULL, characters, value.length());
    }
  }

  default String fromCFString(MemorySegment value) {
    if (isNull(value)) return null;

    var length = Math.toIntExact(CFStringGetLength(value));
    if (length == 0) return "";

    try (var arena = Arena.ofConfined()) {
      var rangeSize = CFRange.MemoryLayout$F.byteSize();
      var buffer = arena.allocate(rangeSize + JAVA_CHAR.byteSize() * length,
          CFRange.MemoryLayout$F.byteAlignment());
      var range = new CFRange(buffer.asSlice(0, rangeSize)).location(0).length(length);
      var characters = buffer.asSlice(rangeSize);
      CFStringGetCharacters(value, range, characters);
      return new String(characters.toArray(JAVA_CHAR));
    }
  }

  private static boolean isNull(MemorySegment value) {
    return value == null || value.equals(MemorySegment.NULL);
  }
}

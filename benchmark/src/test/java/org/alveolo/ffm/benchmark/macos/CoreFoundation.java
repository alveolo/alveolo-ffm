package org.alveolo.ffm.benchmark.macos;

import static org.alveolo.ffm.Library.Kind.FRAMEWORK;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.macos.CFString;

@ForeignInterface
@Library(kind = FRAMEWORK, value = "CoreFoundation")
public interface CoreFoundation {
  long CFStringGetLength(@CFString String value);

  long CFStringGetBytes(@CFString String value, CFRange range,
      int encoding, byte lossByte, boolean externalRepresentation,
      @Out byte[] buffer, long capacity, @Out long[] used);

  @Symbol("CFStringCreateWithCharacters")
  @CFString(owned = true)
  String createCharacters(MemorySegment allocator, @In char[] characters,
      long length);

  @Symbol("CFStringCreateWithCString")
  @CFString(owned = true)
  String create(MemorySegment allocator, String cString, int encoding);
}

@Struct
record CFRange(long location, long length) {}

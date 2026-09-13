package org.alveolo.ffm.benchmark.macos;

import static org.alveolo.ffm.Library.Kind.FRAMEWORK;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.macos.CFRange;
import org.alveolo.ffm.macos.CFString;
import org.alveolo.ffm.macos.CFStringOwned;
import org.alveolo.ffm.macos.CFSupport;

@ForeignInterface
@Library(kind = FRAMEWORK, value = "CoreFoundation")
public interface CoreFoundation extends CFSupport {
  long CFStringGetLength(@CFString String value);

  long CFStringGetBytes(@CFString String value, CFRange range,
      int encoding, byte lossByte, boolean externalRepresentation,
      @Out byte[] buffer, long capacity, @Out long[] used);

  @Symbol("CFStringCreateWithCharacters")
  @CFStringOwned
  String createCharacters(MemorySegment allocator, @In char[] characters,
      long length);

  @Symbol("CFStringCreateWithCString")
  @CFStringOwned
  String create(MemorySegment allocator, String cString, int encoding);

  boolean CFStringFindWithOptions(@CFString String value,
      @CFString String stringToFind, CFRange range, long options,
      @Address CFRange result);
}

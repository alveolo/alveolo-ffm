package pkg;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.macos.CFRange;
import org.alveolo.ffm.macos.CFString;
import org.alveolo.ffm.macos.CFStringOwned;
import org.alveolo.ffm.macos.CFSupport;

@Library(value = "CoreFoundation", kind = Library.Kind.FRAMEWORK)
@ForeignInterface
public interface CoreStrings extends CFSupport {
  long CFStringGetLength(@CFString String value);

  long CFStringGetBytes(@CFString String value, CFRange range,
      int encoding, byte lossByte, boolean externalRepresentation,
      @Out byte[] buffer, long capacity, @Out long[] used);

  @Symbol("CFStringCompare")
  long compare(@CFString String left, @CFString String right, long options);

  @Symbol("CFStringCreateWithCString")
  @CFStringOwned String create(
      MemorySegment allocator, String cString, int encoding);

  boolean CFStringFindWithOptions(@CFString String value,
      @CFString String stringToFind, CFRange range, long options,
      @Address CFRange result);
}

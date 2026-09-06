package pkg;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.macos.CFString;

@Library(value = "CoreFoundation", kind = Library.Kind.FRAMEWORK)
@ForeignInterface
public interface CoreStrings {
  long CFStringGetLength(@CFString String value);

  long CFStringGetBytes(@CFString String value, CFRange range,
      int encoding, byte lossByte, boolean externalRepresentation,
      @Out byte[] buffer, long capacity, @Out long[] used);

  @Symbol("CFStringCompare")
  long compare(@CFString String left, @CFString String right, long options);

  @Symbol("CFStringCreateWithCString")
  @CFString(owned = true) String create(
      MemorySegment allocator, String cString, int encoding);
}

@Struct
record CFRange(long location, long length) {}

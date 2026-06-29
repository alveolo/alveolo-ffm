package pkg;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.macos.CFString;

@Library(value = "CoreFoundation", kind = Library.Kind.FRAMEWORK)
@ForeignInterface
public interface CoreStrings {
  long CFStringGetLength(@CFString String value);

  @Symbol("CFStringCompare")
  long compare(@CFString String left, @CFString String right, long options);

  @Symbol("CFStringCreateWithCString")
  @CFString(owned = true) String create(
      MemorySegment allocator, String cString, int encoding);
}

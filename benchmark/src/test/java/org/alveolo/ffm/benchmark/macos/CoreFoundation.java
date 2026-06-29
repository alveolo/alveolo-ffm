package org.alveolo.ffm.benchmark.macos;

import static org.alveolo.ffm.Library.Kind.FRAMEWORK;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.macos.CFString;

@ForeignInterface
@Library(kind = FRAMEWORK, value = "CoreFoundation")
public interface CoreFoundation {
  long CFStringGetLength(@CFString String value);

  @Symbol("CFStringCreateWithCString")
  @CFString(owned = true)
  String create(MemorySegment allocator, String cString, int encoding);
}

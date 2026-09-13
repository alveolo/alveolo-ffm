package org.alveolo.ffm.benchmark.nativecall;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.macos.CFString;
import org.alveolo.ffm.macos.CFSupport;

@ForeignInterface
@Library("affm_test")
public interface OwnedConversionApi extends OwnedTextSupport, CFSupport {
  @CFString @OwnedCString String owned_text(int kind);

  @Override
  default String fromCFString(MemorySegment pointer) {
    throw new IllegalStateException("conversion failed");
  }
}

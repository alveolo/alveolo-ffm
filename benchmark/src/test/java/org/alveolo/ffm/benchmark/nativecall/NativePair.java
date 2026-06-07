package org.alveolo.ffm.benchmark.nativecall;

import org.alveolo.ffm.ForeignStruct;

@ForeignStruct
public interface NativePair {
  int left();

  int right();
}

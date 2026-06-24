package org.alveolo.ffm.benchmark.nativecall;

import org.alveolo.ffm.ForeignStruct;

@ForeignStruct
public interface PairS {
  int left();

  int right();
}

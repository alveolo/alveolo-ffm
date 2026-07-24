package org.alveolo.ffm.benchmark.affm;

import org.alveolo.ffm.SLong;
import org.alveolo.ffm.Struct;

@Struct
public interface AffmStructSpec {
  int i();

  @SLong
  long cLong();

  long l();
}

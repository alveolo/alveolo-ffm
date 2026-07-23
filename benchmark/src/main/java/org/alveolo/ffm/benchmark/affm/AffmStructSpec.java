package org.alveolo.ffm.benchmark.affm;

import org.alveolo.ffm.CLong;
import org.alveolo.ffm.Struct;

@Struct
public interface AffmStructSpec {
  int i();

  @CLong
  long cLong();

  long l();
}

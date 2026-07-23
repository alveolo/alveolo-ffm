package org.alveolo.ffm.benchmark.affm;

import org.alveolo.ffm.CLong;
import org.alveolo.ffm.Struct;

@Struct
public interface ldiv_t_S {
  @CLong
  long quot();

  @CLong
  long rem();
}

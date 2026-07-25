package org.alveolo.ffm.benchmark.affm;

import org.alveolo.ffm.SLong;
import org.alveolo.ffm.Struct;

@Struct
public interface ldiv_t_S {
  @SLong
  long quot();

  @SLong
  long rem();
}

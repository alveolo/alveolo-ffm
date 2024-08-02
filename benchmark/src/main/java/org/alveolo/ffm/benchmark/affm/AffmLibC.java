package org.alveolo.ffm.benchmark.affm;

import org.alveolo.ffm.ForeignInterface;

@ForeignInterface
public interface AffmLibC {
  int abs(int number);

  IntWrapper abs(IntWrapper number);
}

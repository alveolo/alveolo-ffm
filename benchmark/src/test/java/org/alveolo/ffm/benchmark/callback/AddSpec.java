package org.alveolo.ffm.benchmark.callback;

import org.alveolo.ffm.Callback;

@Callback
interface AddSpec {
  int invoke(int left, int right);
}

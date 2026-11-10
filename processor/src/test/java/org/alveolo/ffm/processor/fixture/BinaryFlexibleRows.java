package org.alveolo.ffm.processor.fixture;

import org.alveolo.ffm.Fields;
import org.alveolo.ffm.Flexible;

@Fields({"count", "data"})
public interface BinaryFlexibleRows {
  short count();
  byte data(@Flexible("count") long i);
}

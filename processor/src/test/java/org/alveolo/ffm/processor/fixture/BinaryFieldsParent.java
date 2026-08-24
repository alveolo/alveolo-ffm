package org.alveolo.ffm.processor.fixture;

import org.alveolo.ffm.Fields;

@Fields({"second", "first"})
public interface BinaryFieldsParent {
  int first();

  long second();
}

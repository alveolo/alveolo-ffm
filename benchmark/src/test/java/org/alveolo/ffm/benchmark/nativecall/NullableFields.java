package org.alveolo.ffm.benchmark.nativecall;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;

@Struct
interface NullableFields {
  PairSFM pair();
}

@Struct
record PrimitivePointer(@Address int value) {}

@Union
interface NullableUnion {
  int value();
}

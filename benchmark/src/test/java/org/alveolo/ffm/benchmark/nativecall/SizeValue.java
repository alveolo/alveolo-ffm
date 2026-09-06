package org.alveolo.ffm.benchmark.nativecall;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.SizeT;
import org.alveolo.ffm.Struct;

@Struct
record SizeValue(@SizeT long value, @Address @SizeT long pointer) {}

@Struct
interface SizeField {
  @SizeT long value();
}

package pkg;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.Struct;

@Struct
record Pair(int left, int right) {}

@Struct
record PairBox(@Address Pair pair) {}

@Struct
record IntBox(@Address int value) {}

@Struct
record Outer(PairBox box, IntBox intBox) {}

package pkg;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.WCharT;

@Struct
record Pair(int left, int right) {}

@Struct
record PairBox(@Address Pair pair) {}

@Struct
record IntBox(@Address @WCharT int value) {}

@Struct
record Outer(PairBox box, IntBox intBox) {}

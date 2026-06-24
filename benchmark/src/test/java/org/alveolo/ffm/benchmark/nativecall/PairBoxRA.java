package org.alveolo.ffm.benchmark.nativecall;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignStruct;

@ForeignStruct
public record PairBoxRA(@Address PairR pair) {}

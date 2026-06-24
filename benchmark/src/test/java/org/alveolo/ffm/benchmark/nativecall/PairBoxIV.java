package org.alveolo.ffm.benchmark.nativecall;

import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.Value;

@ForeignStruct
public record PairBoxIV(@Value PairS pair) {}

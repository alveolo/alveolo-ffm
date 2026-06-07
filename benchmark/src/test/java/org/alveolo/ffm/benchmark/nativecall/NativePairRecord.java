package org.alveolo.ffm.benchmark.nativecall;

import org.alveolo.ffm.ForeignStruct;

@ForeignStruct
public record NativePairRecord(int left, int right) {}

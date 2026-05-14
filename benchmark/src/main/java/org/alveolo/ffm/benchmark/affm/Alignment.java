package org.alveolo.ffm.benchmark.affm;

import org.alveolo.ffm.ForeignStruct;

@ForeignStruct
public record Alignment(boolean b, int i, byte x, char c, long l) {}

package org.alveolo.ffm.benchmark.affm;

import org.alveolo.ffm.SLong;
import org.alveolo.ffm.Struct;

@Struct
public record ldiv_t_R(@SLong long quot, @SLong long rem) {}

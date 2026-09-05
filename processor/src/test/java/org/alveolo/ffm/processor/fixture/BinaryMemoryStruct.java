package org.alveolo.ffm.processor.fixture;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Struct;

/// Compiled without processing to exercise binary component validation.
@Struct
public interface BinaryMemoryStruct {
  int value();
}

/// Minimal compiled wrapper shape; validation must not depend on source rounds.
final class BinaryMemoryWrapper {
  public final MemorySegment MemorySegment$F = MemorySegment.NULL;
}

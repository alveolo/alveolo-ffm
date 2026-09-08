package org.alveolo.ffm.processor.fixture;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Struct;

/// Compiled without processing to exercise binary component validation.
@Struct
public interface BinaryMemoryStruct {
  int value();
}

/// Minimal compiled wrapper shape; validation must not depend on source rounds.
class BinaryMemoryBase {
  public final MemorySegment MemorySegment$F = MemorySegment.NULL;
}

/// Derived wrappers inherit their backing segment from the base wrapper.
final class BinaryMemoryWrapper extends BinaryMemoryBase {}

package org.alveolo.ffm.benchmark.nativecall;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Release;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;

@ForeignInterface
@Library("affm_test")
public interface OwnedRecordApi {
  @Address @Release("release_record") OwnedRecord owned_record(int kind);

  @Address @Release("release_record")
  OwnedRecord owned_record_bad_copy(@Out OwnedPointee[] output);

  @Symbol("owned_record")
  @Address @Release("releaseAndFail") OwnedRecord failingRelease(int kind);

  @Symbol("owned_record")
  @Address @Release("missing_record_release") OwnedRecord missingRelease(int kind);

  void release_record(MemorySegment pointer);

  void missing_record_release(MemorySegment pointer);

  int record_allocations();
  int record_releases();

  default void releaseAndFail(MemorySegment pointer) {
    release_record(pointer);
    throw new IllegalStateException("release failed");
  }
}

@Struct
record OwnedRecord(PairR pair, MemorySegment pointer, @Sequence(2) int[] values) {
  OwnedRecord {
    if (pair.left() < 0) throw new IllegalArgumentException("conversion failed");
  }
}

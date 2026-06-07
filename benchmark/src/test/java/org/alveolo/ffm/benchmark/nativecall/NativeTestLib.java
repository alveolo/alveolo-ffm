package org.alveolo.ffm.benchmark.nativecall;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.ForeignName;
import org.alveolo.ffm.Library;

@Library("alveolo_native_test")
@ForeignInterface
public interface NativeTestLib {
  int add_ints(int left, int right);

  long utf8_bytes(String value);

  @ForeignName("make_pair")
  NativePairRecord make_pair_record(int left, int right);

  int pair_sum(NativePairRecord value);

  @ForeignName("make_pair")
  NativePair make_pair(SegmentAllocator allocator, int left, int right);
}

package org.alveolo.ffm.benchmark.nativecall;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true)
public interface VirtualPairs {
  @Virtual(0)
  @Value PairS make(SegmentAllocator allocator, int left, int right);

  @Virtual(1)
  @Value PairS makeWithError(SegmentAllocator allocator,
      ErrnoSpec capture, int left, int right, int error);
}

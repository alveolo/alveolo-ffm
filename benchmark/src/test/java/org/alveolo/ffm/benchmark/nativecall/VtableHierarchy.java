package org.alveolo.ffm.benchmark.nativecall;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true)
interface VtableBase {}

@Struct
interface VtableMid extends VtableBase {
  @Virtual(0)
  @Value PairS make(SegmentAllocator allocator, int left, int right);
}

@Struct
interface VtableLeaf extends VtableMid {
  @Virtual(1)
  @Value PairS makeWithError(SegmentAllocator allocator,
      ErrnoSpec capture, int left, int right, int error);
}

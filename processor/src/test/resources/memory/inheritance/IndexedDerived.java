package pkg;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;

@Struct
interface IndexedBase {
  int values(@Sequence(2) long index);

  MemorySegment pointers(@Sequence(2) long index);
}

@Struct
public interface IndexedDerived extends IndexedBase {}

final class IndexedDerivedUse {
  static IndexedDerivedFM create(
      java.lang.foreign.SegmentAllocator allocator) {
    return new IndexedDerivedFM(allocator)
        .values(0, 1)
        .valuesFromArray$F(new int[] {2, 3})
        .pointersAsAddress$F(0, MemorySegment.NULL)
        .pointers(1, MemorySegment.NULL);
  }
}

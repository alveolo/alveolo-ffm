package pkg;

import org.alveolo.ffm.Fields;
import org.alveolo.ffm.Struct;

interface Position {
  int x();
  int y();
}

@Fields({"y", "x"})
interface OrderedPosition extends Position {}

@Struct
interface Base {
  short base();
}

@Struct
public interface Derived extends Base, OrderedPosition {
  long own();

  @Override
  int x();
}

final class DerivedUse {
  static DerivedFM create(java.lang.foreign.SegmentAllocator allocator) {
    return new DerivedFM(allocator)
        .base((short) 1)
        .y(2)
        .own(3);
  }
}

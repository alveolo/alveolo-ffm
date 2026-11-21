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
  static DerivedF create(java.lang.foreign.SegmentAllocator allocator) {
    return new DerivedF(allocator)
        .base((short) 1)
        .y(2)
        .own(3);
  }
}

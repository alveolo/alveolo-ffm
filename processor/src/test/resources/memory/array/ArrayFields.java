package pkg;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;
import org.alveolo.ffm.Value;

@Struct
record ArrayPoint(int x, int y) {}

@Struct
interface ArrayCell {
  int value();
}

@Struct
record ArraySnapshot(
    @Sequence(4) byte[] bytes,
    @Sequence(2) ArrayPoint[] points
) {}

@Struct
record ArrayAddressValue(@Address int value) {}

@Struct
record AllocatingArraySnapshot(
    @Sequence(2) ArrayAddressValue[] values
) {}

@Union
interface ArrayUnion {
  short words(@Sequence(4) long index);
}

@Struct
public interface ArrayFields {
  boolean flags(@Sequence(3) int index);

  int matrix(
      @Sequence(2) long row,
      @Sequence(3) long column);

  @Value
  ArrayPoint points(@Sequence(2) long index);

  @Address
  ArrayPoint pointers(@Sequence(2) long index);

  @Value
  ArrayCell cells(@Sequence(2) long index);

  @Address
  ArrayCell references(@Sequence(2) long index);

  MemorySegment raw(@Sequence(2) long index);
}

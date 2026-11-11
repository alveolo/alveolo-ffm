package pkg;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;

@Struct
interface Opaque {}

@Union
interface EmptyUnion {}

@Struct
record EmptyRecord() {}

@ForeignInterface
interface ZeroCalls {
  void snapshots(
      @Address EmptyRecord first,
      @Address EmptyRecord second);
}

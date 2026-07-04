package pkg;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;

@Struct
record RecordDefault(int x) {}

@Address
@Struct
record RecordAddress(int x) {}

@Struct
interface InterfaceDefault {
  int x();
}

@Value
@Struct
interface InterfaceValue {
  int x();
}

@ForeignInterface
public interface PassMode {
  RecordDefault recordDefault(RecordDefault value);

  @Address RecordDefault recordAddress(@Address RecordDefault ref);

  InterfaceDefault interfaceDefault(InterfaceDefault ref);

  @Value InterfaceDefault interfaceValue(
      SegmentAllocator allocator, @Value InterfaceDefault value);

  @Value RecordAddress valueTypeUseOverridesAddressType(
      @Value RecordAddress value);

  @Address
  InterfaceValue addressTypeUseOverridesValueType(@Address InterfaceValue ref);

  int primitiveAddress(@Address int value);

  @Address int primitiveAddressReturn();
}

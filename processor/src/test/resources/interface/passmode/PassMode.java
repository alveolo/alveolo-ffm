package pkg;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.Value;

@ForeignStruct
record RecordDefault(int x) {}

@Address
@ForeignStruct
record RecordAddress(int x) {}

@ForeignStruct
interface InterfaceDefault {
  int x();
}

@Value
@ForeignStruct
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
}

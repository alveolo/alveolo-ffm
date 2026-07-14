package passmode;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Slot;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.Virtual;

@Struct
interface DefaultStruct {
  int value();
}

@Value
@Struct
interface ValueStruct {
  int value();
}

@Address
@Struct
interface AddressStruct {
  int value();
}

@Struct
interface CircularDefaultSpec {
  CircularValue value();
}

@Value
@Struct
interface CircularValueSpec {
  CircularAddress value();
}

@Address
@Struct
interface CircularAddressSpec {
  CircularDefault value();
}

@Struct
record DefaultRecord(int value) {}

@Value
@Struct
record ValueRecord(int value) {}

@Address
@Struct
record AddressRecord(int value) {}

@Struct(vtable = true)
interface VirtualStructSpec {
  @Virtual(0)
  int generatedCircular(
      CircularDefault defaultValue,
      CircularValue value,
      CircularAddress address);

  @Virtual(1)
  int generatedOverrides(
      @Value CircularAddress value,
      @Address CircularValue address);
}

@DispatchTable
interface Functions {
  @Slot(0)
  int generatedInterfaces(
      DefaultStructFM defaultValue,
      ValueStructFM value,
      AddressStructFM address);

  @Slot(1)
  int generatedCircular(
      CircularDefault defaultValue,
      CircularValue value,
      CircularAddress address);

  @Slot(2)
  int originalRecords(
      DefaultRecord defaultValue,
      ValueRecord value,
      AddressRecord address);

  @Slot(3)
  int generatedOverrides(
      @Value AddressStructFM interfaceValue,
      @Address ValueStructFM interfaceAddress);
}

@ForeignInterface
public interface PassMode {
  int originalInterfaces(
      DefaultStruct defaultValue,
      ValueStruct value,
      AddressStruct address);

  int generatedInterfaces(
      DefaultStructFM defaultValue,
      ValueStructFM value,
      AddressStructFM address);

  int originalCircular(
      CircularDefaultSpec defaultValue,
      CircularValueSpec value,
      CircularAddressSpec address);

  int generatedCircular(
      CircularDefault defaultValue,
      CircularValue value,
      CircularAddress address);

  // Record companions are layout/converter utilities, not wrapper instances.
  int originalRecords(
      DefaultRecord defaultValue,
      ValueRecord value,
      AddressRecord address);

  int originalOverrides(
      @Value AddressStruct interfaceValue,
      @Address ValueStruct interfaceAddress,
      @Value AddressRecord recordValue,
      @Address ValueRecord recordAddress);

  int generatedOverrides(
      @Value AddressStructFM interfaceValue,
      @Address ValueStructFM interfaceAddress);

  DefaultRecord originalRecordReturn(DefaultRecord value);

  @Address
  DefaultRecord originalRecordAddressReturn(
      @Address DefaultRecord value);

  DefaultStruct originalInterfaceReturn(DefaultStruct value);

  @Value
  DefaultStruct originalInterfaceValueReturn(
      SegmentAllocator allocator,
      @Value DefaultStruct value);

  int primitiveAddress(@Address int value);

  @Address
  int primitiveAddressReturn();
}

package pkg;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.Value;

@ForeignStruct
record InnerRecord(int x) {}

@ForeignStruct
interface InnerInterface {
  int x();
}

@Address
@ForeignStruct
record TypeAddressRecord(int x) {}

@Value
@ForeignStruct
interface TypeValueInterface {
  int x();
}

@ForeignStruct
public record FieldModes(
    InnerRecord recordDefault,
    InnerInterface interfaceDefault,
    @Address InnerRecord recordTypeUseAddress,
    @Value InnerInterface interfaceTypeUseValue,
    @Value TypeAddressRecord fieldOverridesTypeAddress,
    @Address TypeValueInterface fieldOverridesTypeValue
) {}

@ForeignStruct
interface FieldModeAccessors {
  InnerRecord recordDefault();

  InnerInterface interfaceDefault();

  @Address
  InnerRecord recordTypeUseAddress();

  @Value
  InnerInterface interfaceTypeUseValue();

  @Value
  TypeAddressRecord fieldOverridesTypeAddress();

  @Address
  TypeValueInterface fieldOverridesTypeValue();
}

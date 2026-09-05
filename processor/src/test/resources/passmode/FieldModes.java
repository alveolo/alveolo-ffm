package passmode;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;

@Struct
record InnerRecord(int x) {}

@Struct
interface InnerInterface {
  int x();
}

@Address
@Struct
record TypeAddressRecord(int x) {}

@Value
@Struct
interface TypeValueInterface {
  int x();
}

@Struct
public record FieldModes(
    InnerRecord recordDefault,
    @Address InnerRecord recordTypeUseAddress,
    @Value TypeAddressRecord fieldOverridesTypeAddress
) {}

@Struct
interface FieldModeAccessors {
  InnerRecord recordDefault();

  InnerInterface interfaceDefault();

  @Value
  InnerInterface interfaceTypeUseValue();

  @Value
  TypeAddressRecord fieldOverridesTypeAddress();

  @Address
  TypeValueInterface fieldOverridesTypeValue();
}

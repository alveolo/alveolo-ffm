package pkg;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.SizeT;
import org.alveolo.ffm.Struct;

@Struct
record SizeValue(@SizeT long value, @Address @SizeT long pointer) {}

@Struct
interface SizeField {
  @SizeT long value();
}

@ForeignInterface
public interface Sizes {
  @SizeT long echo(@SizeT long value);

  @SizeT long read(@Address @SizeT long value);

  @SizeT long sum(@Address @SizeT long first, @Address @SizeT long second);

  @Address @SizeT long address();
}

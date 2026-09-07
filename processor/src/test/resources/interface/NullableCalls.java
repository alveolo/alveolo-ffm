package pkg;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Struct;

@Struct
record NullItem(int value) {}

@ForeignInterface
public interface NullableCalls {
  @Address NullItem find();

  @Address int number();

  void accept(@Address NullItem item, String text);
}

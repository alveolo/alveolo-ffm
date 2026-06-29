package pkg;

import org.alveolo.ffm.*;

@Struct
public interface Outer {
  Inner inner();

  int tag();
  Outer tag(int value);
}

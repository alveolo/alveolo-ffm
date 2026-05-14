package pkg;

import org.alveolo.ffm.*;

@ForeignStruct
public interface Outer {
  Inner inner();

  int tag();
  Outer tag(int value);
}

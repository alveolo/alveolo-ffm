package pkg;

import org.alveolo.ffm.*;

@Struct
public interface StructA {
  int x();
  StructA x(int value);

  int y();
  StructA y(int value);
}

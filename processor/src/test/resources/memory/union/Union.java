package pkg;

import org.alveolo.ffm.*;

@ForeignUnion
public interface Union {
  int i();
  Union i(int value);

  double d();
  Union d(double value);
}

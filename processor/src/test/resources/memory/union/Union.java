package pkg;

import org.alveolo.ffm.*;

@org.alveolo.ffm.Union
public interface Union {
  int i();
  Union i(int value);

  double d();
  Union d(double value);
}

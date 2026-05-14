package pkg;

import org.alveolo.ffm.*;

@ForeignStruct
public interface Inner {
  int a();
  Inner a(int value);

  int b();
  Inner b(int value);
}

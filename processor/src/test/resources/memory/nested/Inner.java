package pkg;

import org.alveolo.ffm.*;

@Struct
public interface Inner {
  int a();
  Inner a(int value);

  int b();
  Inner b(int value);
}

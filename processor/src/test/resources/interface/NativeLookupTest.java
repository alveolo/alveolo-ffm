package pkg;

import org.alveolo.ffm.*;

@ForeignInterface
@Library(value = "cups", version = "2")
public interface NativeLookupTest {
  int add_ints(int left, int right);
}

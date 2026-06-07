package pkg;

import org.alveolo.ffm.*;

@Library("alveolo_native_test")
@ForeignInterface
public interface NativeTest {
  int add_ints(int left, int right);
}

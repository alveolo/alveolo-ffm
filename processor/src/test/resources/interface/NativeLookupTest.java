package pkg;

import org.alveolo.ffm.*;

@ForeignInterface
@Library(value = "cups\"\\path\n", version = "2\t",
    overrides = @Library.Override(value = "cups\r\b\f"))
public interface NativeLookupTest {
  @Symbol("add\"ints\\\n\r\t\b\f\0")
  int add_ints(int left, int right);
}

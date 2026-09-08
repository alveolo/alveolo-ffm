package pkg;

import org.alveolo.ffm.Out;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true)
interface ArrayVirtual {
  @Virtual(0)
  void fill(@Out int[] values, int count);

  @Virtual(1)
  int count(int[] values, long length);

  default void fill(int[] values) {
    fill(values, values.length);
  }
}

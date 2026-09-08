package org.alveolo.ffm.benchmark.nativecall;

import java.nio.IntBuffer;

import org.alveolo.ffm.Out;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true)
public interface ArrayVirtual {
  @Virtual(0)
  void fill(@Out int[] values, int count);

  @Virtual(1)
  void fill(@Out IntBuffer values, int count);

  @Virtual(2)
  int count(int[] values, int count);

  default void fill(int[] values) {
    fill(values, values.length);
  }

  default void fill(IntBuffer values) {
    fill(values, values.remaining());
  }

  default int count(int[] values) {
    return count(values, values.length);
  }
}

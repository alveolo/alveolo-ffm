package org.alveolo.ffm.benchmark.nativecall;

import java.nio.IntBuffer;

import org.alveolo.ffm.CountedBy;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true)
public interface CountedVirtual {
  @Virtual(0)
  void fill(@Out @CountedBy("count") int[] values, int count);

  @Virtual(1)
  void fill(@Out @CountedBy("count") IntBuffer values, int count);

  @Virtual(2)
  int count(@CountedBy("count") int[] values, int count);
}

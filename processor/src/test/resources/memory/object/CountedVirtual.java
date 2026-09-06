package pkg;

import org.alveolo.ffm.CountedBy;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true)
interface CountedVirtual {
  @Virtual(0)
  void fill(@Out @CountedBy("count") int[] values, int count);

  @Virtual(1)
  int count(@CountedBy("length") int[] values, long length);
}

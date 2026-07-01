package pkg;

import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Slot;

@DispatchTable
public interface XyzVtbl {
  @Slot(1)
  int add(int a, int b);

  @Slot(index = 3)
  int sub(int a, int b);

  @Slot(index = 2)
  long strlen(String utf8z);

  @Slot(index = 0)
  int sum(@In @Sequence(3L) int[] values);
}

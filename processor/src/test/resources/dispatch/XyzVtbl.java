package pkg;

import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.FirstVariadicArg;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Slot;

@DispatchTable
public interface XyzVtbl {
  @Slot(1)
  int add(int a, int b);

  @Slot(3)
  int sub(int a, int b);

  @Slot(2)
  long strlen(String utf8z);

  @Slot(0)
  int sum(@In @Sequence(3L) int[] values);

  @Slot(4)
  @FirstVariadicArg(1)
  int capturedCall(NativeErrorSpec capture, int parameter);

  @Slot(5)
  int concreteCapturedCall(NativeError capture, int parameter);
}

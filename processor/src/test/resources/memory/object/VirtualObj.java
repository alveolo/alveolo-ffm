package pkg;

import org.alveolo.ffm.In;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true, symbols = NativeApi.class)
public interface VirtualObj {
  int field();

  VirtualObj field(int value);

  @Virtual(2)
  int method(int arg);

  @Virtual(4)
  int sum(@In @Sequence(3L) int[] values);

  @Symbol("native_symbol")
  int call(int arg);
}

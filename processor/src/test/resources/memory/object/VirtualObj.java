package pkg;

import org.alveolo.ffm.FirstVariadicArg;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.SLong;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Virtual;

interface VirtualObjMethods {
  @Virtual(2)
  @FirstVariadicArg(1)
  int method(@SLong long arg);

  @Virtual(4)
  int sum(@In @Sequence(3L) int[] values);

  @Symbol("native_symbol")
  @FirstVariadicArg(1)
  int call(int arg);
}

@Struct(vtable = true, symbols = NativeApi.class)
public interface VirtualObj extends VirtualObjMethods {
  int field();
}

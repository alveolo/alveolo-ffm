package pkg;

import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;

@Struct(symbols = NativeApi.class)
public interface Obj {
  int field();

  @Symbol("native_symbol")
  int call(int arg);

  @Symbol("native_strlen")
  long strlen(String value);
}

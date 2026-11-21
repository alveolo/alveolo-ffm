package test;

import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true, name = "Other")
interface NativeObject {
  @Virtual(0)
  int call();
}

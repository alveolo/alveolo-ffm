package pkg;

import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Virtual;

@Struct(vtable = true)
interface VtableBase {}

@Struct
interface VtableMid extends VtableBase {
  @Virtual(0)
  int first();
}

@Struct
interface VtableLeaf extends VtableMid {
  @Virtual(1)
  int second();
}

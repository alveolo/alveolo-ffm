package pkg;

import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.Slot;

@DispatchTable(name = "RenamedVtblFD")
public interface RenamedVtbl {
  @Slot(0)
  int call(int value);
}

package pkg;

import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.Slot;

@DispatchTable(name = "RenamedVtblF")
public interface RenamedVtbl {
  @Slot(0)
  int call(int value);
}

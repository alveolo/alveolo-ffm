package pkg;

import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.SizeT;
import org.alveolo.ffm.Slot;

interface CommonVtbl {
  int shared(int value);
}

interface MathVtbl extends CommonVtbl {
  @Slot(1)
  int add(int left, int right);
}

interface TextVtbl extends CommonVtbl {
  @Slot(4)
  @SizeT long length(String value);

  default int skipped() { return 0; }
  static int ignored() { return 0; }
}

@DispatchTable
public interface InheritedVtbl extends MathVtbl, TextVtbl {
  @Override
  @Slot(3)
  int shared(int value);

  @Slot(0)
  void reset();
}

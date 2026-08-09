package pkg;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.SizeT;

interface Common {
  int shared(int value);
}

interface Numbers extends Common {
  int number(int value);
}

interface Strings extends Common {
  @SizeT long length(String value);

  default int skipped() { return 0; }
  static int ignored() { return 0; }
}

@ForeignInterface
public interface Inherited extends Numbers, Strings {
  @Override
  int shared(int value);

  void reset();
}

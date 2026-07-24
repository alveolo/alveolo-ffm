package pkg;

import org.alveolo.ffm.*;

@Struct
public interface StructA {
  int x();

  int y();

  @SLong long signed();

  @ULong long unsigned();
}

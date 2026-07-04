package pkg;

import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;

@Struct(name = "RenamedPoint")
public record SimpleOverrides(int x, int y) {}

@Union(name = "RenamedChoice")
interface SimpleChoice {
  int i();
  SimpleChoice i(int value);

  float f();
  SimpleChoice f(float value);
}

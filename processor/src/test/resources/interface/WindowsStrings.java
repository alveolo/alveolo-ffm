package pkg;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.windows.WString;

@ForeignInterface
public interface WindowsStrings {
  void accept(@WString String value);

  void pair(@WString String left, @WString String right);

  @WString String borrowed();
}

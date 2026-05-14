package pkg;

import org.alveolo.ffm.*;
import org.alveolo.ffm.macos.Framework;

@ForeignInterface
@Framework("CoreFoundation")
public interface CoreFramework {
  double CFAbsoluteTimeGetCurrent();
}

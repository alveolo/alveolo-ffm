package pkg;

import org.alveolo.ffm.*;
import org.alveolo.ffm.macos.Framework;

@ForeignInterface
@Framework("CoreFoundation")
@Framework("IOKit")
public interface MultiFramework {}

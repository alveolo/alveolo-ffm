package pkg;

import org.alveolo.ffm.*;

@ForeignInterface
@Library(kind = Library.Kind.FRAMEWORK, value = "CoreFoundation", version = "A")
public interface CoreFramework {
  double CFAbsoluteTimeGetCurrent();
}

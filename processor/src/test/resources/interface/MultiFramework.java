package pkg;

import org.alveolo.ffm.*;

@ForeignInterface
@Library(kind = Library.Kind.FRAMEWORK, value = "CoreFoundation")
@Library(kind = Library.Kind.FRAMEWORK, value = "IOKit")
public interface MultiFramework {}

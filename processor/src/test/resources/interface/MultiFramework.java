package pkg;

import org.alveolo.ffm.*;

@ForeignInterface
@Library(kind = Library.Kind.FRAMEWORK, value = "Security")
@Library(kind = Library.Kind.FRAMEWORK, value = "Accelerate")
public interface MultiFramework {}

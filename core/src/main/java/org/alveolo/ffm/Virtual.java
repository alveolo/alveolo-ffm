package org.alveolo.ffm;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an object method as a native virtual method.
@Target(METHOD)
@Retention(CLASS)
public @interface Virtual {
  /// Pointer index of the method inside the object's dispatch table.
  int value();
}

package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an interface as a C union definition. Fields are inferred from
/// accessor methods.
///
/// @see ForeignStruct
/// @see Sequence
@Target(TYPE)
@Retention(SOURCE)
public @interface ForeignUnion {
  /**
   * Override the generated class name. Defaults to interface name + "FM".
   */
  String name() default "";
}

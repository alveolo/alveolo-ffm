package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an interface for use as a Foreign Function API wrapper
@Target(TYPE)
@Retention(SOURCE)
public @interface ForeignInterface {
  /// Override the generated simple class name. By default, a trailing `Spec` is
  /// removed from an interface name; otherwise the type name is suffixed with
  /// `FFM`.
  String name() default "";
}

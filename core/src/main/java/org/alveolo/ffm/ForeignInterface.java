package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an interface for use as a Foreign Function API wrapper.
///
/// Symbols are resolved once when the generated class initializes. A missing
/// symbol leaves its method unavailable; calling it throws
/// [UnsatisfiedLinkError] with the symbol name before converting arguments.
/// Other methods remain usable.
@Target(TYPE)
@Retention(CLASS)
public @interface ForeignInterface {
  /// Override the generated simple class name. By default, a trailing `Spec` is
  /// removed from an interface name; otherwise the type name is suffixed with
  /// `FFM`.
  String name() default "";
}

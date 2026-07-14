package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an interface as a native dispatch table definition.
///
/// Each abstract method must declare its vtable pointer index with [Slot].
///
/// @see [Slot]
@Target(TYPE)
@Retention(SOURCE)
public @interface DispatchTable {
  /// Override the generated simple class name. By default, a trailing `Spec` is
  /// removed from an interface name; otherwise the type name is suffixed with
  /// `FD`.
  String name() default "";
}

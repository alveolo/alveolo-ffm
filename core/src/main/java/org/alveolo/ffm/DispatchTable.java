package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an interface as a native dispatch table definition.
///
/// Each abstract method must declare its vtable pointer index with [Slot].
///
/// @see Slot
@Target(TYPE)
@Retention(SOURCE)
public @interface DispatchTable {
  /**
   * Override the generated class name. Defaults to interface name + "FD".
   */
  String name() default "";
}

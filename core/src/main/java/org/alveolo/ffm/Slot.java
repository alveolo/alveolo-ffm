package org.alveolo.ffm;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Pointer index of a method inside a native dispatch table.
///
/// The index is expressed in address-sized slots, not bytes.
@Target(METHOD)
@Retention(SOURCE)
public @interface Slot {
  int index();
}

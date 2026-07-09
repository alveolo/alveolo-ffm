package org.alveolo.ffm;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Specifies the length/count for a sequence/buffer layout.
///
/// Can be placed on a type use, method, or parameter.
/// Only primitive arrays and supported NIO buffer types can be sequences.
@Target({TYPE_USE, METHOD, PARAMETER})
@Retention(SOURCE)
public @interface Sequence {
  /// Number of elements in the sequence.
  long value() default 1;
}

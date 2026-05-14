package org.alveolo.ffm;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the length/count for a sequence/buffer layout.
 * Can be placed on a type (TYPE_USE) or on a method (METHOD).
 */
@Target({TYPE_USE, METHOD})
@Retention(SOURCE)
public @interface Sequence {
  long value() default 1;
}

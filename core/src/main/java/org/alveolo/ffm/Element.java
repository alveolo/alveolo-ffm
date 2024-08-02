package org.alveolo.ffm;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;

@Retention(SOURCE)
public @interface Element {
  String name() default "";

  Class<?> type() default void.class;

  long sequence() default 1;
}

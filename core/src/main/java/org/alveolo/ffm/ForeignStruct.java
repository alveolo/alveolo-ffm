package org.alveolo.ffm;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @see Element
 * @see ForeignUnion
 */
@Target(PACKAGE)
@Retention(SOURCE)
@Repeatable(ForeignStruct.List.class)
public @interface ForeignStruct {
  String name() default "";

  Element[] elements() default {};

  @Target(PACKAGE)
  @Retention(SOURCE)
  @interface List {
    ForeignStruct[] value();
  }
}

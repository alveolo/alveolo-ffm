package org.alveolo.ffm;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @see Element
 * @see ForeignStruct
 */
@Target(PACKAGE)
@Retention(SOURCE)
@Repeatable(ForeignUnion.List.class)
public @interface ForeignUnion {
  String name();

  Element[] elements();

  @Target(PACKAGE)
  @Retention(SOURCE)
  @interface List {
    ForeignUnion[] value();
  }
}

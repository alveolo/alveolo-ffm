package org.alveolo.ffm.macos;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Target(TYPE)
@Repeatable(Frameworks.class)
public @interface Framework {
  String value();

  String version() default "Current";
}

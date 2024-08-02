package org.alveolo.ffm.macos;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target(TYPE)
public @interface Frameworks {
  Framework[] value();
}

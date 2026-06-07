package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Loads a native library for use by a Foreign Function API wrapper.
@Target(TYPE)
@Retention(SOURCE)
public @interface Library {
  String value();
}

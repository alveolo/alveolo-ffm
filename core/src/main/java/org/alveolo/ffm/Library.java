package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Loads native symbols for use by a Foreign Function API wrapper.
@Target(TYPE)
@Retention(SOURCE)
@Repeatable(Libraries.class)
public @interface Library {
  /// Logical library name, path, or framework name, depending on
  /// [kind()][#kind()].
  String value() default "";

  /// Optional ABI or framework version.
  String version() default "";

  /// Restricts this library entry to the selected platforms.
  OS[] os() default {};

  Kind kind() default Kind.NAME;

  Override[] overrides() default {};

  @interface Override {
    OS[] os() default {};

    Kind kind() default Kind.NAME;

    String value() default "";
  }

  enum OS {
    MACOS,
    WINDOWS,
    LINUX,
  }

  enum Kind {
    NAME,
    PATH,
    FRAMEWORK,
  }
}

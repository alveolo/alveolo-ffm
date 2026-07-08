package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an interface as a C union definition. Fields are inferred from
/// accessor methods.
///
/// @see [Struct] for C structs
/// @see [Sequence] for fixed-size array/buffer fields.
@Target(TYPE)
@Retention(SOURCE)
public @interface Union {
  /// Override the generated simple class name. The generated class stays in the
  /// same package as the annotated type. Defaults to type name + `FM`.
  String name() default "";
}

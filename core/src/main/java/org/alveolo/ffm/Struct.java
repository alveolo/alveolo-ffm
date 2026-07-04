package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an interface as a C struct definition. Fields are inferred from
/// accessor methods.
///
/// @see Union
/// @see Sequence
@Target(TYPE)
@Retention(SOURCE)
public @interface Struct {
  /// Override the generated simple class name. The generated class stays in the
  /// same package as the annotated type. Defaults to type name + `FM`.
  String name() default "";

  /// Reserve the first struct field for a native dispatch table pointer.
  boolean vtable() default false;

  /// Foreign interface that owns direct native symbols used by this struct.
  Class<?> symbols() default Void.class;
}

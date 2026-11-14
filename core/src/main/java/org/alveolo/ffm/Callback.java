package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Defines a native callback signature.
///
/// The annotated interface must have exactly one abstract method. Its return
/// type and parameters must be primitive types or `MemorySegment`; `void` is
/// also supported as the return type. The generated helper exposes the native
/// function descriptor and creates an upcall stub in a caller-provided arena.
///
/// Exceptions must not escape from a callback invoked by native code. The
/// callback implementation is responsible for handling them according to the
/// native API contract.
@Target(TYPE)
@Retention(SOURCE)
public @interface Callback {
  /// Override the generated simple class name. By default, a trailing `Spec`
  /// is removed from an interface name; otherwise the type name is suffixed
  /// with `F`.
  String name() default "";
}

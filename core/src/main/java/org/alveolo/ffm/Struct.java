package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks a record or interface as a C struct definition. Records provide concise
/// snapshot declarations and cannot contain memory-backed struct/union types or
/// their generated wrappers, even with [Value] or [Address]. This also applies
/// through nested records and record arrays. Use an interface for mixed layouts.
///
/// Direct, otherwise-unmapped
/// interface accessors define fields in declaration order. Reusable parent
/// interfaces can define inherited field placement with [Fields].
///
/// An interface struct may extend one other interface struct. Its generated
/// layout appends fields to the complete base layout, and its generated wrapper
/// extends the base wrapper. Inherited fluent setters are overridden with the
/// derived wrapper return type.
///
/// @see [Union] for C unions
/// @see [Fields] for reusable inherited field mappings
/// @see [Sequence] for fixed-size inline array fields.
@Target(TYPE)
@Retention(CLASS)
public @interface Struct {
  /// Override the generated simple class name. The generated class stays in the
  /// same package as the annotated type. By default, a trailing `Spec` is removed
  /// from an interface name; other interfaces and all records use type name +
  /// `FM`.
  String name() default "";

  /// Reserve the first struct field for a native dispatch table pointer.
  /// The foreign library must initialize the object before it is wrapped.
  /// Vtable structs and their derived structs have no generated allocator
  /// constructor or `allocate$F` helpers. Wrapping an object with a NULL vtable
  /// throws [IllegalArgumentException]. Virtual entries are bound when wrapping;
  /// the object must remain alive and its dispatch table stable while in use.
  boolean vtable() default false;

  /// Foreign interface that owns direct native symbols used by this struct.
  Class<?> symbols() default Void.class;
}

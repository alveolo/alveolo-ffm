package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Defines fields and their order on a reusable interface that is not itself
/// a [Struct] or [Union]. Each name identifies one abstract field accessor
/// family.
///
/// A `@Struct` that inherits this interface uses the listed fields at the
/// point where this mapping first appears in its interface hierarchy.
/// Once a method is mapped, a child may override it without annotations and
/// with the same return type, but cannot map it again as a field, virtual
/// method, or symbol.
///
/// @see [Struct]
@Target(TYPE)
@Retention(CLASS)
public @interface Fields {
  /// Field accessor names in native layout order.
  String[] value();
}

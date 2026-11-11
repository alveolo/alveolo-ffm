package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks a parameter, accessor or class for use as an address or pointer.
/// An empty record passed this way is materialized as a temporary snapshot of
/// its zero-byte layout; it does not retain native pointer identity.
///
/// @see [Value] for pass-by or nested-value semantics.
@Target(TYPE_USE)
@Retention(CLASS)
public @interface Address {}

package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Maps a Java `long` to the platform C `size_t` type.
///
/// The Java carrier remains `long` on every platform. Generated bindings use
/// the native linker's canonical `size_t` layout and adapt its raw carrier when
/// `size_t` is 32 bits. On such platforms, Java values from `0` through
/// `0xffff_ffffL` represent the complete unsigned range; narrowing is checked.
///
/// With a 64-bit `size_t`, Java `long` carries the raw 64-bit representation,
/// including negative values for the upper half of the unsigned range.
///
/// May be combined with [Address] to pass a pointer to `size_t`.
@Target(TYPE_USE)
@Retention(CLASS)
public @interface SizeT {}

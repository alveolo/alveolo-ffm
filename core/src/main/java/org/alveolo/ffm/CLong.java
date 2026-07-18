package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Maps a Java `long` to the platform C `long` type.
///
/// The Java carrier remains `long` on every platform. Generated bindings use
/// the native linker's canonical `long` layout and adapt its raw carrier when
/// C `long` is 32 bits.
///
/// May be combined with [Address] to pass a pointer to C `long`.
@Target(TYPE_USE)
@Retention(CLASS)
public @interface CLong {}

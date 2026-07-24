package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Maps a Java `long` to the platform unsigned C `long` type.
///
/// The Java carrier remains `long` on every platform. Generated bindings use
/// the native linker's canonical `long` layout and adapt its raw carrier when
/// C `unsigned long` is 32 bits. On such platforms, Java values from `0`
/// through `0xffff_ffffL` represent the complete unsigned range.
///
/// On platforms with a 64-bit C `unsigned long`, Java `long` carries the raw
/// 64-bit representation, including negative values for the upper half of the
/// unsigned range.
///
/// May be combined with [Address] to pass a pointer to C `unsigned long`.
@Target(TYPE_USE)
@Retention(CLASS)
public @interface ULong {}

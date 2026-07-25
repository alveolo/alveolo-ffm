package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Maps a Java `int` to the platform C `wchar_t` type.
///
/// The Java carrier remains `int`. Generated bindings adapt to a 16-bit
/// unsigned carrier on platforms such as Windows and retain `int` on platforms
/// with a 32-bit `wchar_t`.
///
/// This annotation describes one scalar value. It does not select an encoding
/// for wide-character strings. May be combined with [Address] to pass a
/// pointer to `wchar_t`.
@Target(TYPE_USE)
@Retention(CLASS)
public @interface WCharT {}

package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Maps a Java `long` to the platform C `size_t` type.
///
/// The native layout is used directly with a Java `long` carrier. Supported
/// 64-bit JDK runtimes expose `size_t` as a 64-bit layout; use Java
/// unsigned-long operations when values above `Long.MAX_VALUE` are meaningful.
///
/// May be combined with [Address] to pass a pointer to `size_t`.
@Target(TYPE_USE)
@Retention(CLASS)
public @interface SizeT {}

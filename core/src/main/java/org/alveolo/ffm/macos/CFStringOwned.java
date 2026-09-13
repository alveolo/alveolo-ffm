package org.alveolo.ffm.macos;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Copies a Create/Copy-rule CFString return and releases the native reference.
/// Equivalent to [CFString] together with [CFRelease].
@CFString
@CFRelease
@Target({TYPE_USE, ANNOTATION_TYPE})
@Retention(CLASS)
public @interface CFStringOwned {}

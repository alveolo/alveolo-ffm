package org.alveolo.ffm.macos;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks a Java String as a CoreFoundation `CFStringRef`.
///
/// Parameters are converted to owned `CFStringRef` values for the native call
/// and released afterwards. Return values are borrowed by default; use `owned =
/// true` for Create/Copy-rule CoreFoundation returns.
@Target(TYPE_USE)
@Retention(CLASS)
public @interface CFString {
  boolean owned() default false;
}

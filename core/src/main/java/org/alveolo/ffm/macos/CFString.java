package org.alveolo.ffm.macos;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.alveolo.ffm.Require;

/// Marks a Java String as a CoreFoundation `CFStringRef`.
///
/// Parameters are converted to owned `CFStringRef` values for the native call
/// and released afterwards. Return values are borrowed; use [CFStringOwned]
/// for Create/Copy-rule returns. The consuming binding supplies the native
/// operations by extending [CFSupport].
@Require(CFSupport.class)
@Target({TYPE_USE, ANNOTATION_TYPE})
@Retention(CLASS)
public @interface CFString {}

package org.alveolo.ffm.macos;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.alveolo.ffm.Release;
import org.alveolo.ffm.Require;

/// Releases an owned CoreFoundation return after conversion using `CFRelease`.
///
/// Composes `@Release("CFRelease")` and `@Require(CFSupport.class)` independently
/// of the conversion annotation. Subject to the return-type restrictions of [Release].
@Release("CFRelease")
@Require(CFSupport.class)
@Target({TYPE_USE, ANNOTATION_TYPE})
@Retention(CLASS)
public @interface CFRelease {}

package org.alveolo.ffm.windows;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks a Java String as a NUL-terminated Windows `WCHAR` string.
///
/// Parameters are encoded as UTF-16LE in temporary native memory for the
/// duration of the call. Native NULL maps to and from Java `null`.
@Target({TYPE_USE, ANNOTATION_TYPE})
@Retention(CLASS)
public @interface WString {}

package org.alveolo.ffm;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Requires the consuming native binding to extend every specified interface.
///
/// Requirements may be declared directly or through composed annotations.
/// They are checked against the actual binding being generated, including its
/// inherited interfaces. Requirements do not add interfaces or load libraries.
@Target({TYPE_USE, ANNOTATION_TYPE})
@Retention(CLASS)
public @interface Require {
  Class<?>[] value();
}

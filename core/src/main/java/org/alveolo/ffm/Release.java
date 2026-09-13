package org.alveolo.ffm;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Releases a native pointer return after conversion to a Java String or record.
/// Record returns must use [Address] pass mode.
///
/// The named binding method must be an instance method accepting one
/// [java.lang.foreign.MemorySegment] and returning void. It receives the
/// original non-NULL pointer, including when conversion or argument copy-out
/// fails. NULL returns map to null and are not released.
/// Only the original return pointer is released; pointer components in a record
/// retain their values and remain the caller's responsibility.
///
/// May be included in a composed annotation. Different effective release
/// methods on the same return are rejected; identical methods release once.
/// Parameters, fields, by-value records, and memory-backed returns are not supported.
@Target({TYPE_USE, ANNOTATION_TYPE})
@Retention(CLASS)
public @interface Release {
  String value();
}

package org.alveolo.ffm;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Limits an array or buffer argument to a count supplied by another
/// parameter of the same native call.
///
/// The named parameter remains an explicit native argument and must have type
/// `byte`, `short`, `int`, or `long`. The generated wrapper validates that its
/// value is between zero and the array length or buffer remaining count, then
/// transfers only that prefix. Direct buffers remain zero-copy and expose a
/// segment view bounded to the prefix.
///
/// Copied storage is valid only for the duration of the native call. Native
/// code that retains the pointer must instead receive caller-managed native
/// storage, such as a direct buffer or a `MemorySegment`.
///
/// `CountedBy` and [Sequence] cannot be combined on the same parameter.
@Target(PARAMETER)
@Retention(SOURCE)
public @interface CountedBy {
  /// Name of the sibling parameter that supplies the element count.
  String value();
}

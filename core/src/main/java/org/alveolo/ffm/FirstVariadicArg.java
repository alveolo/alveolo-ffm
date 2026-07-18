package org.alveolo.ffm;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.foreign.Linker;

/// Marks the first variadic argument in a specialized native call signature.
///
/// The value is the zero-based index among the method's declared native
/// parameters. Java-only parameters such as `SegmentAllocator` and
/// `@CallState` values do not count. When generation inserts a native receiver
/// for an object symbol or virtual call, the processor adjusts the index passed
/// to [Linker.Option#firstVariadicArg(int)].
///
/// Use the native type after C default argument promotion for every argument at
/// or after this index: `int` instead of `boolean`, `byte`, `char`, or `short`,
/// and `double` instead of `float`.
@Target(METHOD)
@Retention(SOURCE)
public @interface FirstVariadicArg {
  /// Zero-based index among the method's declared native parameters.
  int value();
}

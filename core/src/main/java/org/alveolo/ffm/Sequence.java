package org.alveolo.ffm;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Specifies a fixed element count for inline storage or a call carrier.
///
/// On each `int` or `long` parameter of an indexed struct or union field, it
/// declares one positive inline-array dimension. On an array component of a
/// value-style record struct, it declares the exact inline-array extent.
/// On a native-call parameter, the generated wrapper requires the array length
/// or buffer remaining count to equal this value. Use [CountedBy] when another
/// explicit call parameter supplies a variable prefix length.
@Target({TYPE_USE, METHOD, PARAMETER})
@Retention(SOURCE)
public @interface Sequence {
  /// Positive number of elements in the sequence.
  long value() default 1;
}

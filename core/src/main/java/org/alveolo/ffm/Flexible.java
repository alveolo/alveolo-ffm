package org.alveolo.ffm;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks the outer index of the last inline array in a memory-backed [Struct].
/// Its length is supplied when allocating the struct. Inner dimensions use
/// [Sequence], e.g. `byte rows(@Flexible("count") long i, @Sequence(10) int j)`.
/// The wrapper exposes the bounded tail as `rows$MemorySegment$F` in this example.
/// Use [Fields] to share a header. Pass the enclosing struct by address.
@Target(PARAMETER)
@Retention(CLASS)
public @interface Flexible {
  /// Optional integral header field containing the number of outer elements.
  /// Allocation initializes this field. Wrapping existing memory preserves it.
  /// Without an explicit length the wrapper reads the field to determine its capacity.
  /// Explicit lengths use the field's Java type; an unlinked flexible member uses `long`.
  /// AFFM generates only the count getter. The caller must keep the header
  /// count between zero and the allocated capacity during native use.
  /// Ordinary Java integer fields retain their signedness.
  /// With no linked field, callers use generated `allocateFlexible$F` or
  /// `reinterpretFlexible$F` helpers when they know the element count, or
  /// `reinterpret$F` when a native API supplies the complete byte size.
  String value() default "";
}

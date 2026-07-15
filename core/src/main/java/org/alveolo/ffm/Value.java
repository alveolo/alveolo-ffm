package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks a parameter, accessor or class for use as a pass-by or nested value.
///
/// On an array or typed NIO-buffer call parameter, this requests aggregate
/// pass-by-value semantics. Such a parameter must also carry [Sequence] so its
/// native layout is fixed when the downcall handle is created.
///
/// @see [Address] for pointer semantics.
@Target(TYPE_USE)
@Retention(CLASS)
public @interface Value {}

package org.alveolo.ffm;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an array or buffer parameter as input-only.
///
/// The generated wrapper copies values into native memory before the call and
/// does not copy native memory back after the call.
///
/// Direct buffer parameters are always passed directly to native code without
/// copying, so this annotation has no effect for direct buffers.
///
/// @see Out
@Target({PARAMETER, TYPE_USE})
@Retention(SOURCE)
public @interface In {}

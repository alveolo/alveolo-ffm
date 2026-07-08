package org.alveolo.ffm;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an array or buffer parameter as output-only.
///
/// The generated wrapper does not copy Java values into native memory before the
/// call and copies native memory back after the call.
///
/// Direct buffer parameters are always passed directly to native code without
/// copying, so this annotation has no effect for direct buffers.
///
/// @see [In] for input-only parameters.
@Target({PARAMETER, TYPE_USE})
@Retention(SOURCE)
public @interface Out {}

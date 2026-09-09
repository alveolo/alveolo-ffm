package org.alveolo.ffm;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Marks an array or buffer parameter as output-only.
/// Null pointer parameters pass native NULL without allocation or copying.
///
/// The generated wrapper does not copy Java values into native memory before the
/// call and copies native memory back after the call. The entire array or
/// buffer's remaining region is copied, independently of native count arguments
/// or return values.
///
/// Non-empty direct buffer parameters are passed directly to native code without
/// copying, so this annotation has no effect for direct buffers.
///
/// @see [In] for input-only parameters.
@Target({PARAMETER, TYPE_USE})
@Retention(CLASS)
public @interface Out {}

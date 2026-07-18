package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Defines a reusable wrapper for one value captured immediately after a
/// native call.
///
/// The annotated interface must declare exactly one abstract, zero-argument
/// accessor returning `int`. The generated implementation stores a
/// `MemorySegment` allocated with `Linker.Option.captureStateLayout()` and the
/// accessor reads the selected native state from that segment.
///
/// A call-state value can be passed to a foreign call as a Java-only parameter.
/// It is omitted from the native function descriptor and supplied to the
/// downcall handle as its capture-state segment. One instance may be reused by
/// sequential calls, but each call overwrites its previous value. It must be
/// inspected before another call uses the same instance and must not be used
/// concurrently.
@Target(TYPE)
@Retention(SOURCE)
public @interface CallState {
  /// Native capture-state name used unless a platform override matches.
  String value();

  /// Override the generated simple class name. By default, a trailing `Spec`
  /// is removed from an interface name; otherwise the type name is suffixed
  /// with `FM`.
  String name() default "";

  /// Platform-specific native capture-state names.
  Override[] overrides() default {};

  @interface Override {
    /// Platforms on which this override applies. An empty array matches every
    /// platform.
    Library.OS[] os() default {};

    /// Native capture-state name for the selected platforms.
    String value() default "";
  }
}

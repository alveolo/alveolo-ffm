package org.alveolo.ffm;

import static java.lang.foreign.Linker.nativeLinker;

import java.lang.foreign.ValueLayout;

/// Platform-dependent canonical C scalar layouts.
public final class CanonicalLayout {
  public static final ValueLayout LONG = canonicalLayout("long");
  public static final ValueLayout SIZE_T = canonicalLayout("size_t");
  public static final ValueLayout WCHAR_T = canonicalLayout("wchar_t");

  private CanonicalLayout() {/* Utility class */}

  private static ValueLayout canonicalLayout(String name) {
    return (ValueLayout) nativeLinker().canonicalLayouts().get(name);
  }
}

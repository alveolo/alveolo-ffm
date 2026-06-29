package org.alveolo.ffm;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.foreign.SymbolLookup;

/// @see SymbolLookup
@Target(METHOD)
@Retention(SOURCE)
public @interface Symbol {
  String value();
}

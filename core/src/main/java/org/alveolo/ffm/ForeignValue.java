package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @see Address
 */
@Target(TYPE_USE)
@Retention(CLASS)
public @interface ForeignValue {}

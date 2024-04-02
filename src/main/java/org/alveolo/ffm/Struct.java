package org.alveolo.ffm;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target({PACKAGE, TYPE})
public @interface Struct {
	String name() default "";
//	Element[] value();
}

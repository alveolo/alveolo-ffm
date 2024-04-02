package org.alveolo.ffm;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target(TYPE)
public @interface Method {
	String value();
}

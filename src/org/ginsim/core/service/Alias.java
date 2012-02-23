package org.ginsim.core.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention( RetentionPolicy.RUNTIME)
public @interface Alias {

	public static final String NOALIAS = "";
	
	String value() default NOALIAS;
}

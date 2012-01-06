package org.ginsim.gui.service.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention( RetentionPolicy.RUNTIME)
public @interface StandaloneGUI {
   
}
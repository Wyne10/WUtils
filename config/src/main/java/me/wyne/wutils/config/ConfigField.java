package me.wyne.wutils.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigField {
    boolean generate() default false;
    String path() default "";
    String value() default "";
    String comment() default "";
    boolean whitespace() default false;
}

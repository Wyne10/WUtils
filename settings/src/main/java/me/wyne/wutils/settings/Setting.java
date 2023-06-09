package me.wyne.wutils.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply to field to make it accessible as setting via {@link Settings}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {
    /**
     * @return Setting set message
     */
    String setMessage() default "";
    /**
     * @return Reference {@link String} to get {@link Setting} by.
     */
    String reference() default "";
}

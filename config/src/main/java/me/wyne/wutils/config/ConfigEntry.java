package me.wyne.wutils.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field for YAML generation, reading and reloading via {@link me.wyne.wutils.config.Config}.
 *
 * <p>Register the containing object with {@code Config#registerConfigObject(Object)} to collect every
 * annotated field; only fields declared directly on the object's own class are picked up, not ones
 * inherited from a superclass.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {

    /**
     * The section, and optionally one level of {@code primary.sub} nesting, the field is grouped under
     * in the generated config file.
     *
     * <p>Only the {@code primary} segment (before the first {@code .}) is used to build the field's
     * lookup path for reading; the {@code sub} segment only affects how the field is grouped when the
     * config is generated, and any further {@code .}-separated segments are ignored entirely.</p>
     */
    String section();

    /**
     * The YAML key the field is written and read under. Defaults to the field's name.
     */
    String path() default "";

    /**
     * An optional comment rendered above the field's line in the generated config.
     */
    String comment() default "";

    /**
     * Whether the field participates in config reloading/loading.
     */
    boolean load() default true;

}

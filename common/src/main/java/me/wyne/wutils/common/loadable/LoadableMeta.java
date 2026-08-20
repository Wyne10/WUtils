package me.wyne.wutils.common.loadable;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the defaults a {@link Loadable} would otherwise fall back to: the config section
 * path it loads from, its load order relative to other loadables, whether it loads after the
 * main pass, and whether it should be reloaded again on subsequent loads.
 *
 * @see Loadable#getPath()
 * @see Loadable#getPriority()
 * @see Loadable#isLate()
 * @see Loadable#persist()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadableMeta {

    /** Config section path to load from, relative to the {@link Loader}'s root section. */
    @NotNull String path() default Loader.DEFAULT_PATH;

    /** Lower values load first. */
    int priority() default 0;

    boolean late() default false;

    /** Whether the loadable stays registered for reload after its first load. */
    boolean persist() default true;

}

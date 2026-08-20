package me.wyne.wutils.common.loadable;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * A unit of plugin state that a {@link Loader} initializes from a {@link ConfigurationSection}.
 * All defaults below can be overridden per-class with {@link LoadableMeta}.
 */
public interface Loadable {

    /** Initializes this loadable from the config section registered at {@link #getPath()}. */
    void load(@NotNull ConfigurationSection config);

    /**
     * Config section path this loadable reads from, matched against paths registered via
     * {@link Loader#registerConfig}. Defaults to {@link Loader#DEFAULT_PATH}.
     */
    default @NotNull String getPath() {
        if (getClass().isAnnotationPresent(LoadableMeta.class)) {
            return getClass().getAnnotation(LoadableMeta.class).path();
        }
        return Loader.DEFAULT_PATH;
    }

    /** Load order relative to other loadables in the same {@link Loader}; lower values load first. */
    default int getPriority() {
        if (getClass().isAnnotationPresent(LoadableMeta.class)) {
            return getClass().getAnnotation(LoadableMeta.class).priority();
        }
        return 0;
    }

    /** Whether {@link #load} should be deferred to the next server tick instead of running immediately. */
    default boolean isLate() {
        if (getClass().isAnnotationPresent(LoadableMeta.class)) {
            return getClass().getAnnotation(LoadableMeta.class).late();
        }
        return false;
    }

    /** Whether this loadable stays registered for subsequent {@link Loader#load} passes after its first. */
    default boolean persist() {
        if (getClass().isAnnotationPresent(LoadableMeta.class)) {
            return getClass().getAnnotation(LoadableMeta.class).persist();
        }
        return true;
    }

}

package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A unit of work executed against a plugin during one {@link StepScope} of its lifecycle.
 *
 * <p>{@link CompositePlugin} and {@link CompositeJavaPlugin} collect steps and run those matching
 * the current scope in ascending {@link #getPriority()} order, ties broken by registration order.
 * {@link CompositeStep} composes multiple steps into one; {@link ModifiedStep} overrides another
 * step's scope and/or priority without changing its work.</p>
 *
 * @param <T> the plugin type the step runs against
 */
@FunctionalInterface
public interface PluginStep<T extends Plugin> {

    /**
     * Performs this step's work against {@code plugin}.
     */
    void run(@NotNull T plugin);

    /**
     * The scope this step runs in. Defaults to the implementing class's {@link Step @Step}
     * annotation, if present, otherwise {@link StepScope#ENABLE}.
     */
    default @NotNull StepScope getScope() {
        if (getClass().isAnnotationPresent(Step.class)) {
            return getClass().getAnnotation(Step.class).scope();
        }
        return StepScope.ENABLE;
    }

    /**
     * This step's priority among others sharing the same scope. Defaults to the implementing
     * class's {@link Step @Step} annotation, if present, otherwise {@code 0}.
     */
    default int getPriority() {
        if (getClass().isAnnotationPresent(Step.class)) {
            return getClass().getAnnotation(Step.class).priority();
        }
        return 0;
    }
}

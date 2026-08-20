package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link PluginStep} that overrides another step's {@link StepScope scope} and/or priority
 * while delegating its actual work unchanged.
 *
 * <p>Useful for reusing an existing step under a different lifecycle phase or ordering without
 * subclassing it.</p>
 *
 * @param <T> the plugin type the step runs against
 */
public class ModifiedStep<T extends Plugin> implements PluginStep<T> {

    private final PluginStep<T> step;
    private final StepScope scope;
    private final int priority;

    /**
     * Wraps {@code step}, overriding both its scope and its priority.
     */
    public ModifiedStep(@NotNull PluginStep<T> step, @NotNull StepScope scope, int priority) {
        this.step = step;
        this.scope = scope;
        this.priority = priority;
    }

    /**
     * Wraps {@code step}, overriding its scope and keeping its original priority.
     */
    public ModifiedStep(@NotNull PluginStep<T> step, @NotNull StepScope scope) {
        this(step, scope, step.getPriority());
    }

    /**
     * Wraps {@code step}, overriding its priority and keeping its original scope.
     */
    public ModifiedStep(@NotNull PluginStep<T> step, int priority) {
        this(step, step.getScope(), priority);
    }

    @Override
    public void run(@NotNull T plugin) {
        step.run(plugin);
    }

    @Override
    public @NotNull StepScope getScope() {
        return scope;
    }

    @Override
    public int getPriority() {
        return priority;
    }

}

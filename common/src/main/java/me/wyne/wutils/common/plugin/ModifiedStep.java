package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class ModifiedStep<T extends JavaPlugin> implements PluginStep<T> {

    private final PluginStep<T> step;
    private final StepScope scope;
    private final int priority;

    public ModifiedStep(PluginStep<T> step, StepScope scope, int priority) {
        this.step = step;
        this.scope = scope;
        this.priority = priority;
    }

    public ModifiedStep(PluginStep<T> step, StepScope scope) {
        this(step, scope, step.getPriority());
    }

    public ModifiedStep(PluginStep<T> step, int priority) {
        this(step, step.getScope(), priority);
    }

    @Override
    public void run(T plugin) {
        step.run(plugin);
    }

    @Override
    public StepScope getScope() {
        return scope;
    }

    @Override
    public int getPriority() {
        return priority;
    }

}

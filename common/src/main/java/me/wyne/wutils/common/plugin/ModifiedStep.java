package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class ModifiedStep implements PluginStep {

    private final PluginStep step;
    private final StepScope scope;
    private final int priority;

    public ModifiedStep(PluginStep step, StepScope scope, int priority) {
        this.step = step;
        this.scope = scope;
        this.priority = priority;
    }

    public ModifiedStep(PluginStep step, StepScope scope) {
        this(step, scope, step.getPriority());
    }

    public ModifiedStep(PluginStep step, int priority) {
        this(step, step.getScope(), priority);
    }

    @Override
    public void run(JavaPlugin plugin) {
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

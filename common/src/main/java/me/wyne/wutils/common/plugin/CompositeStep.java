package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CompositeStep implements PluginStep {

    private final Set<PluginStep> steps = new TreeSet<>(Comparator.comparing(PluginStep::getPriority));
    private final StepScope scope;
    private final int priority;

    public CompositeStep(StepScope scope, int priority, PluginStep... steps) {
        this.scope = scope;
        this.priority = priority;
        this.steps.addAll(List.of(steps));
    }

    public CompositeStep(PluginStep... steps) {
        this.scope = getScope();
        this.priority = getPriority();
        this.steps.addAll(List.of(steps));
    }

    public void addStep(PluginStep step) {
        steps.add(step);
    }

    public void addSteps(PluginStep... steps) {
        this.steps.addAll(List.of(steps));
    }

    public void before(JavaPlugin plugin) {}
    public void after(JavaPlugin plugin) {}

    @Override
    public final void run(JavaPlugin plugin) {
        before(plugin);
        steps.forEach(step -> step.run(plugin));
        after(plugin);
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

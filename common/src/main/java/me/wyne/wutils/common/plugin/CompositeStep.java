package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.Plugin;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CompositeStep<T extends Plugin> implements PluginStep<T> {

    private final Set<PluginStep<T>> steps = new LinkedHashSet<>();
    private final StepScope scope;
    private final int priority;

    public CompositeStep(StepScope scope, int priority, PluginStep<T>... steps) {
        this.scope = scope;
        this.priority = priority;
        this.steps.addAll(List.of(steps));
    }

    public CompositeStep(PluginStep<T>... steps) {
        this.scope = PluginStep.super.getScope();
        this.priority = PluginStep.super.getPriority();
        this.steps.addAll(List.of(steps));
    }

    public void addStep(PluginStep<T> step) {
        steps.add(step);
    }

    public void addSteps(PluginStep<T>... steps) {
        this.steps.addAll(List.of(steps));
    }

    public void before(T plugin) {}

    public void after(T plugin) {}

    @Override
    public final void run(T plugin) {
        before(plugin);
        steps.stream()
                .sorted(Comparator.comparingInt(PluginStep::getPriority))
                .forEachOrdered(step -> step.run(plugin));
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

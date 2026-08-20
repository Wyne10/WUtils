package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link PluginStep} that groups other steps and runs them together as one step.
 *
 * <p>The group itself has a single {@link StepScope} and priority, used by whatever registers it
 * (e.g. {@link CompositePlugin}) to decide when the whole group runs. Once running, the child
 * steps execute unconditionally in ascending {@link PluginStep#getPriority()} order, ties broken
 * by registration order — a child's own scope is not consulted, since {@link #run} does not
 * filter by scope the way {@link CompositePlugin}'s top-level dispatch does.</p>
 *
 * <p>{@link #run} is final; subclasses customize behavior via {@link #before} and {@link #after}
 * instead, which run immediately before and after the child steps respectively.</p>
 *
 * @param <T> the plugin type the step runs against
 */
public class CompositeStep<T extends Plugin> implements PluginStep<T> {

    private final Set<PluginStep<T>> steps = new LinkedHashSet<>();
    private final StepScope scope;
    private final int priority;

    /**
     * Creates a group with an explicit scope and priority.
     */
    public CompositeStep(@NotNull StepScope scope, int priority, @NotNull PluginStep<T>... steps) {
        this.scope = scope;
        this.priority = priority;
        this.steps.addAll(List.of(steps));
    }

    /**
     * Creates a group defaulting to {@link StepScope#ENABLE} and priority {@code 0}.
     */
    public CompositeStep(@NotNull PluginStep<T>... steps) {
        this.scope = PluginStep.super.getScope();
        this.priority = PluginStep.super.getPriority();
        this.steps.addAll(List.of(steps));
    }

    public void addStep(@NotNull PluginStep<T> step) {
        steps.add(step);
    }

    public void addSteps(@NotNull PluginStep<T>... steps) {
        this.steps.addAll(List.of(steps));
    }

    /**
     * Runs immediately before the child steps. No-op by default.
     */
    public void before(@NotNull T plugin) {}

    /**
     * Runs immediately after the child steps. No-op by default.
     */
    public void after(@NotNull T plugin) {}

    @Override
    public final void run(@NotNull T plugin) {
        before(plugin);
        steps.stream()
                .sorted(Comparator.comparingInt(PluginStep::getPriority))
                .forEachOrdered(step -> step.run(plugin));
        after(plugin);
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

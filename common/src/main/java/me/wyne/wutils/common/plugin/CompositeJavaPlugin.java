package me.wyne.wutils.common.plugin;

import me.wyne.wutils.common.scheduler.Schedulers;
import me.wyne.wutils.common.terminable.TerminableConsumer;
import me.wyne.wutils.common.terminable.composite.CompositeTerminable;
import me.wyne.wutils.common.terminable.module.TerminableModule;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Base {@link JavaPlugin} implementation adding the {@link Step}/{@link StepScope} pipeline and a
 * bound {@link TerminableConsumer} registry. This is the class plugins built on WUtils extend
 * directly, as {@code class MyPlugin extends CompositeJavaPlugin<MyPlugin>}.
 *
 * <p>Steps are registered via {@link #addStep}/{@link #addSteps} or by annotating a no-argument
 * method with {@link Step @Step}; either way they run against {@code this}, cast to {@code T}.
 * {@link #onLoad()} collects annotated steps, calls {@link #init()}, creates the terminable
 * registry, then runs the {@code LOAD} steps. {@link #onEnable()} schedules an async task that
 * periodically cleans up completed/cancelled bindings in the registry, then runs the
 * {@code ENABLE} steps. {@link #onDisable()} runs the {@code DISABLE} steps first, then closes
 * the terminable registry, logging any exceptions encountered while closing — so disable steps
 * may still use bindings registered via {@link #bind}/{@link #bindModule}. {@link #reload()} is
 * not called by Bukkit; call it to fire a {@link PluginReloadEvent} and run the {@code RELOAD}
 * steps.</p>
 *
 * <p>{@link #bind} and {@link #bindModule} are only safe to call once {@link #onLoad()} has run,
 * since the backing registry is created there.</p>
 *
 * @param <T> the concrete plugin subtype, used so steps and terminable bindings see the actual
 *            plugin type rather than {@code CompositeJavaPlugin}
 */
public abstract class CompositeJavaPlugin<T extends Plugin> extends JavaPlugin implements TerminableConsumer {

    private CompositeTerminable terminableRegistry;

    private final Set<PluginStep<T>> steps = new LinkedHashSet<>();

    private void loadAnnotations() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Step.class))
                addStep(new AnnotationStep<>(method));
        }
    }

    /**
     * Hook invoked once during {@link #onLoad()}, after annotated steps are collected but before
     * the terminable registry is created or any step runs. Override to register additional steps
     * or perform other one-time setup.
     */
    public void init() {}

    @Override
    public void onLoad() {
        loadAnnotations();
        init();
        this.terminableRegistry = CompositeTerminable.create();
        runSteps(StepScope.LOAD);
    }

    @Override
    public void onEnable() {
        Schedulers.builder()
                .async()
                .after(10, TimeUnit.SECONDS)
                .every(30, TimeUnit.SECONDS)
                .run(this.terminableRegistry::cleanup)
                .bindWith(this.terminableRegistry);
        runSteps(StepScope.ENABLE);
    }

    @Override
    public void onDisable() {
        runSteps(StepScope.DISABLE);
        this.terminableRegistry.closeAndReportException();
    }

    /**
     * Fires a {@link PluginReloadEvent} and runs every {@link StepScope#RELOAD} step. Not invoked
     * automatically; call this from a reload command or similar.
     */
    public void reload() {
        new PluginReloadEvent(this).callEvent();
        runSteps(StepScope.RELOAD);
    }

    /**
     * Registers a step to run when its scope is reached.
     */
    public void addStep(@NotNull PluginStep<T> step) {
        steps.add(step);
    }

    public void addSteps(@NotNull PluginStep<T>... steps) {
        this.steps.addAll(Set.of(steps));
    }

    private void runSteps(StepScope scope) {
        steps.stream()
                .filter(step -> step.getScope() == scope)
                .sorted(Comparator.comparingInt(PluginStep::getPriority))
                .forEachOrdered(step -> step.run((T) this));
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return this.terminableRegistry.bind(terminable);
    }

    @Nonnull
    @Override
    public <T extends TerminableModule> T bindModule(@Nonnull T module) {
        return this.terminableRegistry.bindModule(module);
    }

}

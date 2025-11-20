package me.wyne.wutils.common.plugin;

import me.wyne.wutils.common.scheduler.Schedulers;
import me.wyne.wutils.common.terminable.TerminableConsumer;
import me.wyne.wutils.common.terminable.composite.CompositeTerminable;
import me.wyne.wutils.common.terminable.module.TerminableModule;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class CompositeJavaPlugin<T extends Plugin> extends JavaPlugin implements TerminableConsumer {

    private CompositeTerminable terminableRegistry;

    private final Set<PluginStep<T>> steps = new LinkedHashSet<>();

    private void loadAnnotations() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Step.class))
                addStep(new AnnotationStep<>(method));
        }
    }

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

    public void reload() {
        new PluginReloadEvent(this).callEvent();
        runSteps(StepScope.RELOAD);
    }

    public void addStep(PluginStep<T> step) {
        steps.add(step);
    }

    public void addSteps(PluginStep<T>... steps) {
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

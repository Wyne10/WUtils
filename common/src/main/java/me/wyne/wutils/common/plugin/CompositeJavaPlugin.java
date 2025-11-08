package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class CompositeJavaPlugin<T extends Plugin> extends JavaPlugin {

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
        runSteps(StepScope.LOAD);
    }

    @Override
    public void onEnable() {
        runSteps(StepScope.ENABLE);
    }

    @Override
    public void onDisable() {
        runSteps(StepScope.DISABLE);
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

}

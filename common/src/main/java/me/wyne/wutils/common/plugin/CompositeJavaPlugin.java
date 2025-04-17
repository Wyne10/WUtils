package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class CompositeJavaPlugin<T extends JavaPlugin> extends JavaPlugin {

    private final Set<PluginStep<T>> steps = new TreeSet<>(Comparator.comparing(PluginStep::getPriority));

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
        runSteps(StepScope.RELOAD);
    }

    public void addStep(PluginStep<T> step) {
        steps.add(step);
    }

    public void addSteps(PluginStep<T>... steps) {
        this.steps.addAll(List.of(steps));
    }

    private void runSteps(StepScope scope) {
        steps.stream()
                .filter(step -> step.getScope() == scope)
                .forEachOrdered(step -> step.run((T) this));
    }

}

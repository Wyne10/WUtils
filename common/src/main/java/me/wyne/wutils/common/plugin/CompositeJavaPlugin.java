package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class CompositeJavaPlugin extends JavaPlugin {

    private final Set<PluginStep> loadingSteps = new TreeSet<>(Comparator.comparing(PluginStep::getPriority));

    private void loadAnnotations() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Step.class))
                addStep(new AnnotationStep(method));
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

    public void addStep(PluginStep step) {
        loadingSteps.add(step);
    }

    public void addSteps(PluginStep... steps) {
        loadingSteps.addAll(List.of(steps));
    }

    private void runSteps(StepScope scope) {
        loadingSteps.stream()
                .filter(step -> step.getScope() == scope)
                .forEachOrdered(step -> step.run(this));
    }

}

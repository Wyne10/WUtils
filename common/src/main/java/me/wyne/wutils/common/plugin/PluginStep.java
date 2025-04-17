package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

@FunctionalInterface
public interface PluginStep {
    void run(JavaPlugin plugin);
    default StepScope getScope() {
        if (getClass().isAnnotationPresent(Step.class)) {
            return getClass().getAnnotation(Step.class).scope();
        }
        return StepScope.ENABLE;
    }
    default int getPriority() {
        if (getClass().isAnnotationPresent(Step.class)) {
            return getClass().getAnnotation(Step.class).priority();
        }
        return 0;
    }
}

package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotationStep<T extends JavaPlugin> implements PluginStep<T> {

    private final Method method;
    private final Step annotation;

    public AnnotationStep(Method method) {
        this.method = method;
        this.annotation = method.getAnnotation(Step.class);
    }

    @Override
    public void run(T plugin) {
        method.setAccessible(true);
        try {
            method.invoke(plugin);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke composite plugin step: " + method.getName(), e);
        }
    }

    @Override
    public StepScope getScope() {
        return annotation.scope();
    }

    @Override
    public int getPriority() {
        return annotation.priority();
    }

}

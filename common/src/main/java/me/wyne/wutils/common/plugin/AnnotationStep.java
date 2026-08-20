package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A {@link PluginStep} backed by a no-argument, {@link Step @Step}-annotated method, invoked
 * reflectively on the plugin instance.
 *
 * <p>{@link CompositePlugin} and {@link CompositeJavaPlugin} create one of these for every
 * declared method carrying {@code @Step}, so a step can be written as a plain annotated method
 * instead of a standalone {@link PluginStep} class. The method may be of any visibility; it is
 * made accessible before invocation.</p>
 *
 * @param <T> the plugin type the step runs against
 */
public class AnnotationStep<T extends Plugin> implements PluginStep<T> {

    private final Method method;
    private final Step annotation;

    /**
     * Wraps {@code method}, which must carry a {@link Step @Step} annotation.
     */
    public AnnotationStep(@NotNull Method method) {
        this.method = method;
        this.annotation = method.getAnnotation(Step.class);
    }

    /**
     * Invokes the wrapped method on {@code plugin} with no arguments.
     *
     * @throws RuntimeException if the method is inaccessible or itself throws
     */
    @Override
    public void run(@NotNull T plugin) {
        method.setAccessible(true);
        try {
            method.invoke(plugin);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke composite plugin step " + method.getName(), e);
        }
    }

    @Override
    public @NotNull StepScope getScope() {
        return annotation.scope();
    }

    @Override
    public int getPriority() {
        return annotation.priority();
    }

}

package me.wyne.wutils.common.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a unit of plugin bootstrap/lifecycle work and the {@link StepScope} it runs in.
 *
 * <p>Has two uses depending on target. Applied to a no-argument method of a
 * {@link CompositePlugin} or {@link CompositeJavaPlugin} subclass, it is picked up automatically
 * and wrapped as an {@link AnnotationStep}. Applied to a class implementing {@link PluginStep},
 * it supplies that class's default {@link PluginStep#getScope()} and
 * {@link PluginStep#getPriority()}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Step {

    /**
     * The lifecycle phase this step runs in.
     */
    StepScope scope();

    /**
     * Ordering among steps sharing the same scope; steps run in ascending priority order, ties
     * broken by registration order.
     */
    int priority() default 0;

}

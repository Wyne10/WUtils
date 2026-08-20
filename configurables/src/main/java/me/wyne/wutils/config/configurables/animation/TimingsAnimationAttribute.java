package me.wyne.wutils.config.configurables.animation;

import org.jetbrains.annotations.NotNull;

/**
 * An animation step timing — {@code delay}, {@code period} or {@code duration} — that writes its
 * parsed tick value onto a shared {@link AnimationTimings}.
 */
public interface TimingsAnimationAttribute {
    /**
     * Writes this attribute's tick value onto {@code timings}, mutating it in place.
     */
    void apply(@NotNull AnimationTimings timings);
}

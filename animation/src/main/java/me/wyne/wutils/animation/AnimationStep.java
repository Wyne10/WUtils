package me.wyne.wutils.animation;

import org.jetbrains.annotations.NotNull;

/**
 * A single stage of an {@link Animation}.
 *
 * <p>The first step is started by the owning {@link Animation}; every step thereafter is
 * started by its predecessor. A step drives its {@link AnimationRunnable} on a schedule and
 * then hands off to the next queued step (see the concrete implementations in this package
 * for how, and on which thread, that happens).</p>
 *
 * <p>{@link #close()} and {@link #_finalize()} both default to a no-op; implementations only
 * need to override the one relevant to how they hold resources.</p>
 */
@FunctionalInterface
public interface AnimationStep extends AutoCloseable, Finalizable {
    /**
     * Starts this step as part of the given animation.
     */
    void run(@NotNull Animation animation);
    @Override
    default void close() {}
    @Override
    default void _finalize() {}
}

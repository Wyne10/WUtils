package me.wyne.wutils.animation;

import org.jetbrains.annotations.NotNull;

/**
 * The unit of work executed by an {@link AnimationStep} on each invocation.
 *
 * <p>Implementations are typically the effect records in the {@code runnable} package
 * (particles, sounds, titles, ...). {@link Runnable#run()} is the sole abstract method, and
 * is what an effect that ignores timing implements. Steps always call
 * {@link #run(long, long, long)}, which by default discards the timing values and delegates
 * to {@link #run()}; an implementation that needs them overrides it instead, as
 * {@link CompositeRunnable} does.</p>
 */
@FunctionalInterface
public interface AnimationRunnable extends Runnable {
    /**
     * Invoked by the owning step on every tick it fires (once, for a non-repeating step).
     *
     * @param delay the step's initial delay, in ticks
     * @param period the step's repeat period, in ticks, or {@code 0} if it does not repeat
     * @param duration the step's total duration, in ticks
     */
    default void run(long delay, long period, long duration) {
        run();
    }

    /**
     * A runnable that does nothing.
     */
    @NotNull AnimationRunnable EMPTY = () -> {};

    /**
     * @deprecated {@link AnimationRunnable} is a functional interface, so an existing
     * {@link Runnable} can be adapted directly with a method reference (e.g.
     * {@code runnable::run}) without going through this factory.
     */
    @Deprecated
    static @NotNull AnimationRunnable runnable(@NotNull Runnable runnable) {
        return runnable::run;
    }
}

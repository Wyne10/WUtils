package me.wyne.wutils.animation;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Combines several {@link AnimationRunnable}s so a single {@link AnimationStep} can drive
 * them together, as if they were one runnable.
 *
 * <p>{@link #close()} and {@link #_finalize()} delegate to whichever wrapped runnables
 * implement {@link AutoCloseable} / {@link Finalizable} respectively, so a composite behaves
 * like any other closeable/finalizable runnable from the owning step's point of view.</p>
 */
public record CompositeRunnable(@NotNull Collection<@NotNull AnimationRunnable> runnables) implements AnimationRunnable, AutoCloseable, Finalizable {

    @Override
    public void run(long delay, long period, long duration) {
        runnables.forEach(runnable -> runnable.run(delay, period, duration));
    }

    // Never invoked by the animation framework, which always calls run(long, long, long);
    // present only to satisfy Runnable.
    @Override
    public void run() {

    }

    @Override
    public void close() {
        runnables.stream()
                .filter(runnable -> runnable instanceof AutoCloseable)
                .map(runnable -> (AutoCloseable) runnable)
                .forEach(autoCloseable -> {
                    try {
                        autoCloseable.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void _finalize() {
        runnables.stream()
                .filter(runnable -> runnable instanceof Finalizable)
                .map(runnable -> (Finalizable) runnable)
                .forEach(Finalizable::_finalize);
    }

}

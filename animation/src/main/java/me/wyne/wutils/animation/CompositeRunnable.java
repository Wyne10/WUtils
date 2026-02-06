package me.wyne.wutils.animation;

import java.util.Collection;

public record CompositeRunnable(Collection<AnimationRunnable> runnables) implements AnimationRunnable, AutoCloseable {

    @Override
    public void run(long delay, long period, long duration) {
        runnables.forEach(runnable -> runnable.run(delay, period, duration));
    }

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

}

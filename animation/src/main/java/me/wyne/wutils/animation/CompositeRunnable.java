package me.wyne.wutils.animation;

import java.util.Collection;

public record CompositeRunnable(Collection<AnimationRunnable> runnables) implements AnimationRunnable {

    @Override
    public void run(long delay, long period, long duration) {
        runnables.forEach(runnable -> runnable.run(delay, period, duration));
    }

    @Override
    public void close() {
        runnables.forEach(AnimationRunnable::close);
    }

}

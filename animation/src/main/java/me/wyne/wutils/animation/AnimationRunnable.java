package me.wyne.wutils.animation;

@FunctionalInterface
public interface AnimationRunnable extends Runnable {
    default void run(long delay, long period, long duration) {
        run();
    }

    AnimationRunnable EMPTY = () -> {};

    @Deprecated
    static AnimationRunnable runnable(Runnable runnable) {
        return runnable::run;
    }
}

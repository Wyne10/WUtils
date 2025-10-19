package me.wyne.wutils.animation;

public interface AnimationRunnable extends Runnable, AutoCloseable {
    default void run(long delay, long period, long duration) {
        run();
    }
    @Override
    default void run() {}
    @Override
    default void close() {}

    AnimationRunnable EMPTY = new AnimationRunnable() {};

    static AnimationRunnable runnable(Runnable runnable) {
        return new AnimationRunnable() {
            @Override
            public void run(long delay, long period, long duration) {
                runnable.run();
            }
        };
    }
}

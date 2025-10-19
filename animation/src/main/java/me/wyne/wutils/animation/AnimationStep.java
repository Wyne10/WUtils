package me.wyne.wutils.animation;

@FunctionalInterface
public interface AnimationStep extends AutoCloseable {
    void run(Animation animation);
    @Override
    default void close() {}
}

package me.wyne.wutils.animation;

@FunctionalInterface
public interface AnimationStep extends AutoCloseable, Finalizable {
    void run(Animation animation);
    @Override
    default void close() {}
    @Override
    default void _finalize() {}
}

package me.wyne.wutils.animation;

/**
 * Marks a type that must release resources when it is discarded without ever having run.
 *
 * <p>This is the counterpart to {@link AutoCloseable#close()}: {@code close()} tears down
 * something that was started, while {@code _finalize()} tears down something that was queued
 * but never got the chance to start. See {@link Animation#stop()}, which calls {@code close()}
 * on steps that had started and {@code _finalize()} on the ones still waiting in the queue.</p>
 */
@FunctionalInterface
public interface Finalizable {
    void _finalize();
}

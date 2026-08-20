package me.wyne.wutils.structure;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A thread-safe cancellation flag passed into {@link Structure#create}. Cancelling it fails the
 * in-flight {@link WorldStructure} future rather than letting it retry indefinitely.
 */
public record StructureCancellationToken(@NotNull AtomicBoolean cancelled) {
    public StructureCancellationToken() {
        this(new AtomicBoolean());
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    public void cancel() {
        cancelled.set(true);
    }
}

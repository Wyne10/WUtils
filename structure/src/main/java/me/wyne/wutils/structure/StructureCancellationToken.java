package me.wyne.wutils.structure;

import java.util.concurrent.atomic.AtomicBoolean;

public record StructureCancellationToken(AtomicBoolean cancelled) {
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

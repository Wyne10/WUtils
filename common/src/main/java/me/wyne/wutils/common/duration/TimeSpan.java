package me.wyne.wutils.common.duration;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public record TimeSpan(long duration, Duration type) implements Duration {
    public long getMillis() {
        return type.getMillis(duration);
    }

    public long getTicks() {
        return type.getTicks(duration);
    }

    public long getUnit(TimeUnit unit) {
        return unit.convert(getMillis(duration), TimeUnit.MILLISECONDS);
    }

    @Override
    public long getMillis(long duration) {
        return type.getMillis(duration);
    }

    @Override
    public long getTicks(long duration) {
        return type.getTicks(duration);
    }

    @Override
    public long getUnit(long duration, TimeUnit unit) {
        return unit.convert(getMillis(duration), TimeUnit.MILLISECONDS);
    }

    @Override
    public @NotNull String toString() {
        return duration + Durations.getSymbol(type);
    }
}

package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.Ticks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * {@link Duration} unit for milliseconds. {@link #getTicks} truncates towards zero
 * for values that aren't a multiple of {@link Ticks#MILLIS_PER_TICK}.
 */
public class Millis implements Duration {
    @Override
    public long getMillis(long duration) {
        return duration;
    }

    @Override
    public long getTicks(long duration) {
        return Ticks.ofMillis(duration);
    }

    @Override
    public long getUnit(long duration, @NotNull TimeUnit unit) {
        return unit.convert(duration, TimeUnit.MILLISECONDS);
    }
}

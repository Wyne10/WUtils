package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.Ticks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * {@link Duration} unit for hours. {@link #getTicks} is exact, assuming
 * {@link Ticks#TICKS_PER_SECOND} ticks per second.
 */
public class Hours implements Duration {
    @Override
    public long getMillis(long duration) {
        return duration * 60 * 60 * 1000;
    }

    @Override
    public long getTicks(long duration) {
        return Ticks.ofSeconds(duration * 60 * 60);
    }

    @Override
    public long getUnit(long duration, @NotNull TimeUnit unit) {
        return unit.convert(duration, TimeUnit.HOURS);
    }
}

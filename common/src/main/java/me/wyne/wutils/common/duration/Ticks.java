package me.wyne.wutils.common.duration;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * {@link Duration} unit for server ticks, the {@link Durations} registry default for a
 * missing or unrecognized unit symbol. Distinct from
 * {@link me.wyne.wutils.common.Ticks}, the plain utility class holding the raw
 * tick/millisecond conversion math ({@code 1 tick = 50ms}) that this class delegates
 * to; that class has no unit-registry role and does not implement {@link Duration}.
 */
public class Ticks implements Duration {
    @Override
    public long getMillis(long duration) {
        return me.wyne.wutils.common.Ticks.toMillis(duration);
    }

    @Override
    public long getTicks(long duration) {
        return duration;
    }

    @Override
    public long getUnit(long duration, @NotNull TimeUnit unit) {
        return unit.convert(getMillis(duration), TimeUnit.MILLISECONDS);
    }
}

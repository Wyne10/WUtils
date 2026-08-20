package me.wyne.wutils.common;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Raw conversions between Minecraft server ticks and other time units, assuming a
 * fixed rate of {@value #TICKS_PER_SECOND} ticks per second ({@value #MILLIS_PER_TICK}ms
 * per tick). Distinct from {@link me.wyne.wutils.common.duration.Ticks}, the
 * {@link me.wyne.wutils.common.duration.Duration} implementation that delegates its
 * math to this class for use with {@link me.wyne.wutils.common.duration.Durations} and
 * {@link me.wyne.wutils.common.duration.TimeSpan}; this class has no unit-registry
 * role of its own. {@link #duration(long)} returns a {@link java.time.Duration}, not a
 * {@link me.wyne.wutils.common.duration.Duration}.
 * <p>
 * Conversions from ticks to a coarser unit truncate towards zero.
 */
public final class Ticks {

    public final static long MILLIS_PER_TICK = 50;
    public final static long TICKS_PER_SECOND = 20;
    public final static long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    public final static long TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;

    /** Converts milliseconds to ticks, truncating towards zero. */
    public static long ofMillis(long durationMillis) {
        return durationMillis / MILLIS_PER_TICK;
    }

    /** Converts seconds to ticks; exact. */
    public static long ofSeconds(long durationSeconds) {
        return durationSeconds * TICKS_PER_SECOND;
    }

    public static long of(long duration, @NotNull TimeUnit unit) {
        return unit.toMillis(duration) / MILLIS_PER_TICK;
    }

    /** Converts ticks to milliseconds; exact. */
    public static long toMillis(long ticks) {
        return ticks * MILLIS_PER_TICK;
    }

    /** Converts ticks to seconds, truncating towards zero for non-multiples of {@value #TICKS_PER_SECOND}. */
    public static long toSeconds(long ticks) {
        return toMillis(ticks) / 1000;
    }

    /**
     * Converts ticks to an arbitrary {@link TimeUnit} by first truncating to whole
     * seconds via {@link #toSeconds(long)}. For units finer than seconds (e.g.
     * milliseconds) this discards any sub-second remainder from {@code ticks} — use
     * {@link #toMillis(long)} directly when millisecond precision matters.
     */
    public static long to(long ticks, @NotNull TimeUnit unit) {
        return unit.convert(toMillis(ticks), TimeUnit.MILLISECONDS);
    }

    /** Converts ticks to a {@link java.time.Duration}; exact. */
    public static @NotNull Duration duration(long ticks) {
        return Duration.ofMillis(toMillis(ticks));
    }

}

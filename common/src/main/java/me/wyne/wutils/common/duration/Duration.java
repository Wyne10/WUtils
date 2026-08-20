package me.wyne.wutils.common.duration;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * A time unit that converts a raw {@code duration} value expressed in that unit to
 * milliseconds, server ticks, or an arbitrary {@link TimeUnit}.
 * <p>
 * {@link Millis}, {@link Seconds}, {@link Minutes}, {@link Hours}, {@link Days} and
 * {@link Ticks} are the implementations; {@link Durations} looks them up by symbol and
 * parses compound duration strings. Unrelated to {@code me.wyne.wutils.common.Ticks},
 * which holds the raw tick/millisecond math this package's {@link Ticks} delegates to,
 * and to {@link java.time.Duration}.
 */
public interface Duration {
    long getMillis(long duration);
    long getTicks(long duration);
    long getUnit(long duration, @NotNull TimeUnit unit);
}

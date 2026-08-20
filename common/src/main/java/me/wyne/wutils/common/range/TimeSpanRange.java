package me.wyne.wutils.common.range;

import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import me.wyne.wutils.common.range.iterator.TimeSpanIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A closed range of {@link TimeSpan} values {@code [min, max]}, inclusive of both bounds,
 * compared and stored in milliseconds. The constructor normalizes its arguments, so
 * {@code min} and {@code max} may be given in either order.
 * <p>
 * Iterates in ascending order via {@link TimeSpanIterator} with a fixed step of one second.
 */
public class TimeSpanRange extends Range<TimeSpan> {

    public TimeSpanRange(@NotNull TimeSpan min, @NotNull TimeSpan max) {
        super(new TimeSpan(Math.min(min.getMillis(), max.getMillis()), Durations.Millis),
                new TimeSpan(Math.max(min.getMillis(), max.getMillis()), Durations.Millis),
                new TimeSpan((min.getMillis() + max.getMillis()) / 2, Durations.Millis),
                new TimeSpan(max.getMillis() - min.getMillis(), Durations.Millis));
    }

    /**
     * Returns a random time span in {@code [min, max]}, both bounds inclusive.
     */
    @Override
    public @NotNull TimeSpan getRandom() {
        return new TimeSpan(ThreadLocalRandom.current().nextLong(getMin().getMillis(), getMax().getMillis() + 1), Durations.Millis);
    }

    @Override
    public boolean contains(@NotNull TimeSpan value) {
        return value.getMillis() >= getMin().getMillis() && value.getMillis() <= getMax().getMillis();
    }

    @Override
    public @NotNull Iterator<@NotNull TimeSpan> iterator() {
        return new TimeSpanIterator(getMin(), getMax(), new TimeSpan(1, Durations.Seconds));
    }

    @Override
    public @NotNull String toString() {
        return getMin() + ".." + getMax();
    }

    /**
     * Parses a range from {@code "min..max"}, where each side is a duration string accepted by
     * {@link Durations#getTimeSpan}.
     */
    public static @NotNull TimeSpanRange getTimeSpanRange(@NotNull String string) {
        var split = string.split("\\.\\.");
        return new TimeSpanRange(Durations.getTimeSpan(split[0]), Durations.getTimeSpan(split[1]));
    }

}

package me.wyne.wutils.common.range.iterator;

import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates {@link TimeSpan}s from {@code start} to {@code end}, comparing and advancing in
 * whole milliseconds. A positive step iterates ascending and requires {@code start <= end}; a
 * negative step iterates descending and requires {@code start >= end}. If the direction implied
 * by {@code step} does not match {@code start}/{@code end}, {@link #hasNext()} is {@code false}
 * immediately and no elements are yielded.
 * <p>
 * Unlike {@link DoubleRangeIterator}, accumulation is done in {@code long} milliseconds, so
 * there is no floating-point drift: {@code end} is yielded exactly when {@code (end - start)}
 * is an exact multiple of {@code step}, otherwise the last value yielded is the closest one
 * strictly before {@code end} in the iteration direction. A {@code step} of zero milliseconds
 * never satisfies the termination check and iterates forever.
 */
public class TimeSpanIterator implements Iterator<TimeSpan> {

    private long currentMillis;
    private final long endMillis;
    private final long stepMillis;

    public TimeSpanIterator(@NotNull TimeSpan start, @NotNull TimeSpan end, @NotNull TimeSpan step) {
        this.currentMillis = start.getMillis();
        this.endMillis = end.getMillis();
        this.stepMillis = step.getMillis();
    }

    @Override
    public boolean hasNext() {
        return (stepMillis > 0) ? currentMillis <= endMillis : currentMillis >= endMillis;
    }

    @Override
    public @NotNull TimeSpan next() {
        if (!hasNext())
            throw new NoSuchElementException();
        TimeSpan value = new TimeSpan(currentMillis, Durations.Millis);
        currentMillis += stepMillis;
        return value;
    }

}

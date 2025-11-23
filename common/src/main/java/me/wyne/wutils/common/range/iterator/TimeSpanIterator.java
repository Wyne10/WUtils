package me.wyne.wutils.common.range.iterator;

import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TimeSpanIterator implements Iterator<TimeSpan> {

    private long currentMillis;
    private final long endMillis;
    private final long stepMillis;

    public TimeSpanIterator(TimeSpan start, TimeSpan end, TimeSpan step) {
        this.currentMillis = start.getMillis();
        this.endMillis = end.getMillis();
        this.stepMillis = step.getMillis();
    }

    @Override
    public boolean hasNext() {
        return (stepMillis > 0) ? currentMillis <= endMillis : currentMillis >= endMillis;
    }

    @Override
    public TimeSpan next() {
        if (!hasNext())
            throw new NoSuchElementException();
        TimeSpan value = new TimeSpan(currentMillis, Durations.Millis);
        currentMillis += stepMillis;
        return value;
    }

}

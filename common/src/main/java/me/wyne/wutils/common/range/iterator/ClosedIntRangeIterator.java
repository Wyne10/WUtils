package me.wyne.wutils.common.range.iterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates integers from {@code start} to {@code end}, inclusive of {@code end}, advancing by
 * {@code step} each call. A positive step iterates ascending and requires {@code start <= end};
 * a negative step iterates descending and requires {@code start >= end}. If the direction
 * implied by {@code step} does not match {@code start}/{@code end}, {@link #hasNext()} is
 * {@code false} immediately and no elements are yielded.
 * <p>
 * A {@code step} of {@code 0} never satisfies the termination check and iterates forever.
 */
public class ClosedIntRangeIterator implements Iterator<Integer> {

    private int current;
    private final int end;
    private final int step;

    public ClosedIntRangeIterator(int start, int end, int step) {
        this.current = start;
        this.end = end;
        this.step = step;
    }

    @Override
    public boolean hasNext() {
        return (step > 0) ? current <= end : current >= end;
    }

    @Override
    public @NotNull Integer next() {
        if (!hasNext())
            throw new NoSuchElementException();
        int value = current;
        current += step;
        return value;
    }

}

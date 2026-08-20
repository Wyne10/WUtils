package me.wyne.wutils.common.range.iterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates doubles from {@code start} to {@code end} inclusive, {@code step} apart. A positive
 * step iterates ascending and requires {@code start <= end}; a negative step iterates descending
 * and requires {@code start >= end}. If the direction implied by {@code step} does not match
 * {@code start}/{@code end}, {@link #hasNext()} is {@code false} immediately and no elements are
 * yielded.
 * <p>
 * The element count is computed once up front and each value is derived as
 * {@code start + index * step}, so floating-point error does not accumulate across the iteration
 * and {@code end} is yielded exactly when it is a whole number of steps away. See {@link Steps}
 * for what that does and does not guarantee.
 * <p>
 * A {@code step} of {@code 0} still never terminates.
 */
public class DoubleRangeIterator implements Iterator<Double> {

    private final double start;
    private final double end;
    private final double step;
    private final long count;

    private long index;

    public DoubleRangeIterator(double start, double end, double step) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.count = Steps.count(start, end, step);
    }

    @Override
    public boolean hasNext() {
        return index < count;
    }

    @Override
    public @NotNull Double next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return Steps.at(start, end, step, index++);
    }

}

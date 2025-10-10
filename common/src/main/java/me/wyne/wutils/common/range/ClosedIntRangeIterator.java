package me.wyne.wutils.common.range;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
    public Integer next() {
        if (!hasNext())
            throw new NoSuchElementException();
        int value = current;
        current += step;
        return value;
    }

}

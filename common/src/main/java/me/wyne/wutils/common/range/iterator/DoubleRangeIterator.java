package me.wyne.wutils.common.range.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleRangeIterator implements Iterator<Double> {

    private double current;
    private final double end;
    private final double step;

    public DoubleRangeIterator(double start, double end, double step) {
        this.current = start;
        this.end = end;
        this.step = step;
    }

    @Override
    public boolean hasNext() {
        return (step > 0) ? current <= end : current >= end;
    }

    @Override
    public Double next() {
        if (!hasNext())
            throw new NoSuchElementException();
        double value = current;
        current += step;
        return value;
    }

}

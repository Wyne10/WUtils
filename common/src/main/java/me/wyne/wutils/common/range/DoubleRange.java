package me.wyne.wutils.common.range;

import me.wyne.wutils.common.range.iterator.DoubleRangeIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class DoubleRange extends Range<Double> {

    public DoubleRange(Double min, Double max) {
        super(Math.min(min, max), Math.max(min, max), (min + max) / 2, max - min);
    }

    @Override
    public Double getRandom() {
        return ThreadLocalRandom.current().nextDouble(getMin(), getMax());
    }

    @Override
    public boolean contains(Double value) {
        return value >= getMin() && value <= getMax();
    }

    @Override
    public @NotNull Iterator<Double> iterator() {
        return new DoubleRangeIterator(getMin(), getMax(), 1);
    }

    @Override
    public String toString() {
        return getMin() + ".." + getMax();
    }

    public static DoubleRange getDoubleRange(String string) {
        var split = string.split("\\.\\.");
        return new DoubleRange(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }

}

package me.wyne.wutils.common.range;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class ClosedIntRange extends Range<Integer> {

    public ClosedIntRange(Integer min, Integer max) {
        super(Math.min(min, max), Math.max(min, max), (min + max) / 2, max - min);
    }

    @Override
    public Integer getRandom() {
        return ThreadLocalRandom.current().nextInt(getMin(), getMax() + 1);
    }

    @Override
    public Integer getCenter() {
        return (getMin() + getMax()) / 2;
    }

    @Override
    public boolean contains(Integer value) {
        return value >= getMin() && value <= getMax();
    }

    @Override
    public @NotNull Iterator<Integer> iterator() {
        return new ClosedIntRangeIterator(getMin(), getMax(), 1);
    }

}

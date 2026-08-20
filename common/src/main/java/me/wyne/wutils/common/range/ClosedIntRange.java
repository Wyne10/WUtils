package me.wyne.wutils.common.range;

import me.wyne.wutils.common.range.iterator.ClosedIntRangeIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A closed integer range {@code [min, max]}, inclusive of both bounds. The constructor
 * normalizes its arguments, so {@code min} and {@code max} may be given in either order.
 * <p>
 * Iterates in ascending order, one step at a time, via {@link ClosedIntRangeIterator}.
 */
public class ClosedIntRange extends Range<Integer> {

    public ClosedIntRange(@NotNull Integer min, @NotNull Integer max) {
        super(Math.min(min, max), Math.max(min, max), (min + max) / 2, max - min);
    }

    /**
     * Returns a random integer in {@code [min, max]}, both bounds inclusive.
     */
    @Override
    public @NotNull Integer getRandom() {
        return ThreadLocalRandom.current().nextInt(getMin(), getMax() + 1);
    }

    @Override
    public boolean contains(@NotNull Integer value) {
        return value >= getMin() && value <= getMax();
    }

    @Override
    public @NotNull Iterator<@NotNull Integer> iterator() {
        return new ClosedIntRangeIterator(getMin(), getMax(), 1);
    }

    @Override
    public @NotNull String toString() {
        return getMin() + ".." + getMax();
    }

    /**
     * Parses a range from {@code "min..max"}.
     */
    public static @NotNull ClosedIntRange getIntRange(@NotNull String string) {
        var split = string.split("\\.\\.");
        return new ClosedIntRange(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

}

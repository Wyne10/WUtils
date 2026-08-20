package me.wyne.wutils.common.range;

import me.wyne.wutils.common.range.iterator.DoubleRangeIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * A closed double range {@code [min, max]}, inclusive of both bounds per {@link #contains} and
 * {@link #getRandom()}. The constructor normalizes its arguments, so {@code min} and {@code max}
 * may be given in either order.
 * <p>
 * Iterates via {@link DoubleRangeIterator} with a fixed step of {@code 1.0}; see that class for
 * the floating-point step hazards of iterating a double range in general.
 */
public class DoubleRange extends Range<Double> {

    public DoubleRange(@NotNull Double min, @NotNull Double max) {
        super(Math.min(min, max), Math.max(min, max), (min + max) / 2, max - min);
    }

    /** Returns a random double in {@code [min, max]}; a single-point range always returns its value. */
    @Override
    public @NotNull Double getRandom() {
        return randomInclusive(getMin(), getMax());
    }

    @Override
    public boolean contains(@NotNull Double value) {
        return value >= getMin() && value <= getMax();
    }

    @Override
    public @NotNull Iterator<@NotNull Double> iterator() {
        return new DoubleRangeIterator(getMin(), getMax(), 1);
    }

    @Override
    public @NotNull String toString() {
        return getMin() + ".." + getMax();
    }

    /**
     * Parses a range from {@code "min..max"}.
     */
    public static @NotNull DoubleRange getDoubleRange(@NotNull String string) {
        var split = string.split("\\.\\.");
        return new DoubleRange(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }

}

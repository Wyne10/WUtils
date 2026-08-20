package me.wyne.wutils.common.range;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Base for a range of values of type {@code T}, exposing a minimum, maximum, center and span,
 * plus random sampling, containment and iteration.
 * <p>
 * This class stores the four bounds exactly as given, without validating or reordering them.
 * Subclasses are responsible for normalizing {@code min}/{@code max} order before calling the
 * constructor, and for defining what "contains", "random" and iteration order mean for
 * {@code T} — see the concrete subclasses ({@link ClosedIntRange}, {@link DoubleRange},
 * {@link VectorRange}, {@link LocationRange}, {@link TimeSpanRange}) for their specific
 * inclusivity and traversal contracts.
 *
 * @param <T> the value type of the range
 */
public abstract class Range<T> implements Iterable<T> {

    private final T min;
    private final T center;
    private final T max;
    private final T span;

    public Range(@NotNull T min, @NotNull T max, @NotNull T center, @NotNull T span) {
        this.min = min;
        this.max = max;
        this.center = center;
        this.span = span;
    }

    /**
     * Samples uniformly from the closed interval {@code [min, max]}.
     *
     * <p>{@link ThreadLocalRandom#nextDouble(double, double)} cannot be used directly for a closed
     * interval: it requires {@code origin < bound}, so it throws on a single-point or inverted
     * interval, and it excludes the bound, so it can never return {@code max}. This returns
     * {@code min} for a degenerate interval and otherwise widens the bound by one ulp, clamping
     * the result so {@code max} is reachable but never exceeded.</p>
     *
     * <p>Public so the Kotlin range extensions in {@code wutils-common-kotlin} sample Kotlin's
     * {@code ClosedFloatingPointRange} through this same implementation rather than repeating it.</p>
     */
    public static double randomInclusive(double min, double max) {
        if (!(min < max))
            return min;
        double bound = Math.nextUp(max);
        // Guards max == Double.MAX_VALUE, where nextUp is infinite and would poison the draw.
        if (!Double.isFinite(bound))
            bound = max;
        return Math.min(ThreadLocalRandom.current().nextDouble(min, bound), max);
    }

    public @NotNull T getMin() {
        return min;
    }

    public @NotNull T getCenter() {
        return center;
    }

    public @NotNull T getMax() {
        return max;
    }

    public @NotNull T getSpan() {
        return span;
    }

    /**
     * Returns a random value within this range. Whether the maximum bound itself is reachable
     * depends on the implementation.
     */
    public abstract @NotNull T getRandom();

    /**
     * Returns whether {@code value} lies within this range.
     */
    public abstract boolean contains(@NotNull T value);

}

package me.wyne.wutils.common.range.iterator;

/**
 * Shared step arithmetic for the floating-point range iterators.
 *
 * <p>Walking a double range by accumulating {@code current += step} drifts, because the error of
 * each addition is carried into the next one. Two things go wrong as a result: an endpoint can be
 * skipped entirely — {@code 0.0..0.3} by {@code 0.1} stops at {@code 0.2}, since the third
 * addition lands on {@code 0.30000000000000004} — and the values yielded are not the ones the
 * caller asked for.</p>
 *
 * <p>Computing {@code start + index * step} instead keeps the error to a single rounding no matter
 * how far along the iteration has gone. It does not make every value exactly the decimal the
 * caller had in mind — {@code 3 * 0.2} is still not {@code 0.6} in binary floating point — but the
 * error stays bounded rather than growing, the element count is correct, and the bound is hit
 * exactly.</p>
 */
final class Steps {

    /** Relative slack for deciding whether an extent is a whole number of steps. */
    private static final double TOLERANCE = 1e-9;

    private Steps() {}

    /**
     * Counts the values {@code start}, {@code start + step}, ... that fall within
     * {@code [start, end]} (or {@code [end, start]} for a negative step).
     *
     * <p>The comparison is made with a relative tolerance, so an extent that divides evenly in
     * decimal is treated as doing so here too rather than losing its final element to rounding.</p>
     *
     * @return {@code 0} when {@code step} points away from {@code end}, so a range whose direction
     *         is inverted is empty rather than yielding a partial element; {@link Long#MAX_VALUE}
     *         for a zero step, which never terminates
     */
    static long count(double start, double end, double step) {
        if (step == 0)
            return Long.MAX_VALUE;
        double spans = (end - start) / step;
        if (spans < 0)
            return 0;
        return (long) Math.floor(spans + TOLERANCE * Math.max(1.0, spans)) + 1;
    }

    /**
     * Returns the value at {@code index} in the sequence, snapped to {@code end} when it lands
     * there, so an inclusive range yields its bound exactly instead of a value one ulp short.
     */
    static double at(double start, double end, double step, long index) {
        double value = start + index * step;
        return Math.abs(value - end) <= TOLERANCE * Math.max(1.0, Math.abs(end)) ? end : value;
    }

}

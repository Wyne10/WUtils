package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * {@link Operable} for {@code int} arithmetic. {@link #divide} throws
 * {@link ArithmeticException} on a zero divisor, and {@link #power} computes via
 * {@link Math#pow(double, double)} and truncates the result to {@code int}, so
 * fractional or negative exponents are lossy.
 */
public class IntOperations implements Operable<Integer> {
    @Override
    public @NotNull Integer add(@NotNull Integer first, @NotNull Integer second) {
        return first + second;
    }

    @Override
    public @NotNull Integer subtract(@NotNull Integer minuend, @NotNull Integer subtrahend) {
        return minuend - subtrahend;
    }

    @Override
    public @NotNull Integer multiply(@NotNull Integer multiplicand, @NotNull Integer multiplier) {
        return multiplicand * multiplier;
    }

    @Override
    public @NotNull Integer divide(@NotNull Integer dividend, @NotNull Integer divisor) {
        return dividend / divisor;
    }

    @Override
    public @NotNull Integer power(@NotNull Integer base, @NotNull Integer exponent) {
        return (int) Math.pow(base, exponent);
    }
}

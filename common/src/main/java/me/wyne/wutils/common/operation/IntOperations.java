package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * {@link Operable} for {@code int} arithmetic. {@link #divide} and {@link #modulo} throw
 * {@link ArithmeticException} on a zero divisor, {@link #divide} truncates toward zero,
 * {@link #modulo} takes the sign of its dividend, and {@link #power} casts the
 * {@link Math#pow(double, double)} result back to {@code int}, so a negative exponent
 * truncates to {@code 0} (or {@code ±1} for a base of {@code ±1}).
 */
public class IntOperations implements Operable<Integer> {
    @Override
    public @NotNull Integer add(@NotNull Integer augend, @NotNull Integer addend) {
        return augend + addend;
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

    @Override
    public @NotNull Integer modulo(@NotNull Integer dividend, @NotNull Integer divisor) {
        return dividend % divisor;
    }

    @Override
    public @NotNull Integer min(@NotNull Integer first, @NotNull Integer second) {
        return Math.min(first, second);
    }

    @Override
    public @NotNull Integer max(@NotNull Integer first, @NotNull Integer second) {
        return Math.max(first, second);
    }
}

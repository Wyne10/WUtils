package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * {@link Operable} for {@code double} arithmetic. Division and remainder by zero follow
 * IEEE 754 rather than throwing: {@link #divide} yields {@code Infinity} or {@code NaN}
 * and {@link #modulo} yields {@code NaN}. {@link #min} and {@link #max} are
 * {@link Math#min(double, double)}/{@link Math#max(double, double)}, so a {@code NaN}
 * operand propagates and {@code -0.0} is treated as smaller than {@code 0.0}.
 */
public class DoubleOperations implements Operable<Double> {
    @Override
    public @NotNull Double add(@NotNull Double augend, @NotNull Double addend) {
        return augend + addend;
    }

    @Override
    public @NotNull Double subtract(@NotNull Double minuend, @NotNull Double subtrahend) {
        return minuend - subtrahend;
    }

    @Override
    public @NotNull Double multiply(@NotNull Double multiplicand, @NotNull Double multiplier) {
        return multiplicand * multiplier;
    }

    @Override
    public @NotNull Double divide(@NotNull Double dividend, @NotNull Double divisor) {
        return dividend / divisor;
    }

    @Override
    public @NotNull Double power(@NotNull Double base, @NotNull Double exponent) {
        return Math.pow(base, exponent);
    }

    @Override
    public @NotNull Double modulo(@NotNull Double dividend, @NotNull Double divisor) {
        return dividend % divisor;
    }

    @Override
    public @NotNull Double min(@NotNull Double first, @NotNull Double second) {
        return Math.min(first, second);
    }

    @Override
    public @NotNull Double max(@NotNull Double first, @NotNull Double second) {
        return Math.max(first, second);
    }
}

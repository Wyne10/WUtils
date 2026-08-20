package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * {@link Operable} for {@code double} arithmetic. Division by zero follows IEEE 754
 * semantics ({@code Infinity}/{@code NaN}) instead of throwing.
 */
public class DoubleOperations implements Operable<Double> {
    @Override
    public @NotNull Double add(@NotNull Double first, @NotNull Double second) {
        return first + second;
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
}

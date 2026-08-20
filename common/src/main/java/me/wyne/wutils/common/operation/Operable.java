package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Defines the actual arithmetic for a specific boxed {@link Number} type. {@link Operation}
 * implementations dispatch here via {@link Operations#getOperations(Number)} rather than
 * computing directly, so a single {@link Plus}/{@link Minus}/etc. works for every supported
 * type.
 * <p>
 * {@link IntOperations} and {@link DoubleOperations} are the two implementations, and they
 * disagree on edge cases: division by zero throws for {@code int} but yields
 * {@code Infinity}/{@code NaN} for {@code double}, and {@link #power} truncates its result
 * to {@code int} in the integer implementation.
 */
public interface Operable<T extends Number> {
    @NotNull T add(@NotNull T first, @NotNull T second);
    @NotNull T subtract(@NotNull T minuend, @NotNull T subtrahend);
    @NotNull T multiply(@NotNull T multiplicand, @NotNull T multiplier);
    @NotNull T divide(@NotNull T dividend, @NotNull T divisor);
    @NotNull T power(@NotNull T base, @NotNull T exponent);
}

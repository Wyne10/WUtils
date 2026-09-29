package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Defines the actual arithmetic for a specific boxed {@link Number} type. {@link Operation}
 * implementations dispatch here via {@link Operations#getOperations(Number)} rather than
 * computing directly, so a single {@link Plus}/{@link Minus}/etc. works for every supported
 * type.
 * <p>
 * {@link IntOperations} and {@link DoubleOperations} are the two implementations, and they
 * disagree on edge cases: {@link #divide} and {@link #modulo} by zero throw for {@code int}
 * but yield {@code Infinity}/{@code NaN} for {@code double}, and {@link #power} truncates its
 * result to {@code int} in the integer implementation.
 * <p>
 * {@link #modulo} is Java's {@code %} in both implementations, so it is a remainder and takes
 * the sign of its dividend. {@link #min} and {@link #max} are the only non-arithmetic members:
 * they select one operand rather than combining them, which is what makes {@link Min}/{@link Max}
 * usable as clamps.
 */
public interface Operable<T extends Number> {
    @NotNull T add(@NotNull T augend, @NotNull T addend);
    @NotNull T subtract(@NotNull T minuend, @NotNull T subtrahend);
    @NotNull T multiply(@NotNull T multiplicand, @NotNull T multiplier);
    @NotNull T divide(@NotNull T dividend, @NotNull T divisor);
    @NotNull T power(@NotNull T base, @NotNull T exponent);
    @NotNull T modulo(@NotNull T dividend, @NotNull T divisor);
    @NotNull T min(@NotNull T first, @NotNull T second);
    @NotNull T max(@NotNull T first, @NotNull T second);
}

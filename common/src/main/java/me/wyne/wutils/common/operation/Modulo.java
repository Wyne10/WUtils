package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Remainder: {@code leftOperand % rightOperand}. This is Java's {@code %}, so the sign
 * of the result follows the <em>dividend</em> rather than the divisor — {@code -7 % 3}
 * is {@code -1}, not {@code 2}. A zero right operand throws for {@code int} and yields
 * {@code NaN} for {@code double}; see {@link Operable}.
 */
public class Modulo<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return Operations.getOperations(leftOperand).modulo(leftOperand, rightOperand);
    }
}

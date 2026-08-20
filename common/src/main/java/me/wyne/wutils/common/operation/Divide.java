package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Division: {@code leftOperand / rightOperand}. Division by zero throws for
 * {@code int} and yields {@code Infinity}/{@code NaN} for {@code double}; see
 * {@link Operable}.
 */
public class Divide<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return Operations.getOperations(leftOperand).divide(leftOperand, rightOperand);
    }
}

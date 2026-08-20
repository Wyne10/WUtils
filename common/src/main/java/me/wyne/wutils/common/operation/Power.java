package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Exponentiation: {@code leftOperand ^ rightOperand}. The {@code int} implementation
 * truncates the {@link Math#pow(double, double)} result; see {@link Operable}.
 */
public class Power<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return Operations.getOperations(leftOperand).power(leftOperand, rightOperand);
    }
}

package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Multiplication: {@code leftOperand * rightOperand}.
 */
public class Multiply<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return Operations.getOperations(leftOperand).multiply(leftOperand, rightOperand);
    }
}

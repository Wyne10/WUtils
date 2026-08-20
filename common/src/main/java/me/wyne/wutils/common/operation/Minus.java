package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Subtraction: {@code leftOperand - rightOperand}.
 */
public class Minus<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return Operations.getOperations(leftOperand).subtract(leftOperand, rightOperand);
    }
}

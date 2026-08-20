package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Addition: {@code leftOperand + rightOperand}.
 */
public class Plus<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return Operations.getOperations(leftOperand).add(leftOperand, rightOperand);
    }
}

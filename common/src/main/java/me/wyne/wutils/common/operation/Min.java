package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Minimum: the smaller of {@code leftOperand} and {@code rightOperand}. Used as an
 * upper clamp — {@code "<10"} caps a value at {@code 10} and leaves anything smaller
 * alone. Note that the {@code <} symbol means <em>less than</em> on the comparator
 * side; see {@link Operations#getOperation(String)}.
 */
public class Min<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return Operations.getOperations(leftOperand).min(leftOperand, rightOperand);
    }
}

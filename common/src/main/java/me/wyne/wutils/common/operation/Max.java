package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Maximum: the larger of {@code leftOperand} and {@code rightOperand}. Used as a
 * lower clamp — {@code ">0"} floors a value at {@code 0} and leaves anything larger
 * alone. Note that the {@code >} symbol means <em>greater than</em> on the comparator
 * side; see {@link Operations#getOperation(String)}.
 */
public class Max<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return Operations.getOperations(leftOperand).max(leftOperand, rightOperand);
    }
}

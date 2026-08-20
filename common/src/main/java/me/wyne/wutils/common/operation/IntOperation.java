package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link ContainedOperation} for {@code int} values, e.g. produced by
 * {@link Operations#getIntOperation(String)} from strings like {@code "+5"}.
 */
public record IntOperation(int rightOperand, @NotNull Operation<Integer> operation) implements ContainedOperation<Integer> {

    @Override
    public @NotNull Integer evaluate(@NotNull Integer leftOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

    @Override
    public @NotNull Integer evaluate(@NotNull Integer leftOperand, @NotNull Integer rightOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

    @Override
    public @NotNull String toString() {
        return Operations.getOperator(operation) + rightOperand;
    }

}

package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link ContainedOperation} for {@code double} values, e.g. produced by
 * {@link Operations#getDoubleOperation(String)} from strings like {@code "+1.5"}.
 */
public record DoubleOperation(double rightOperand, @NotNull Operation<Double> operation) implements ContainedOperation<Double> {

    @Override
    public @NotNull Double evaluate(@NotNull Double leftOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

    @Override
    public @NotNull Double evaluate(@NotNull Double leftOperand, @NotNull Double rightOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

    @Override
    public @NotNull String toString() {
        return Operations.getOperator(operation) + rightOperand;
    }

}

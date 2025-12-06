package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

public record DoubleOperation(double rightOperand, Operation<Double> operation) implements ContainedOperation<Double> {

    @Override
    public Double evaluate(Double leftOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

    @Override
    public Double evaluate(Double leftOperand, Double rightOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

    @Override
    public @NotNull String toString() {
        return Operations.getOperator(operation) + rightOperand;
    }

}

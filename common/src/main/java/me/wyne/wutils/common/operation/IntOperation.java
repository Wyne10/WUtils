package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

public record IntOperation(int rightOperand, Operation<Integer> operation) implements ContainedOperation<Integer> {

    @Override
    public Integer evaluate(Integer leftOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

    @Override
    public Integer evaluate(Integer leftOperand, Integer rightOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

    @Override
    public @NotNull String toString() {
        return Operations.getOperator(operation) + rightOperand;
    }

}

package me.wyne.wutils.common.operation;

public record IntOperation(int rightOperand, Operation<Integer> operation) implements ContainedOperation<Integer> {

    @Override
    public Integer evaluate(Integer leftOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

}

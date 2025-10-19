package me.wyne.wutils.common.operation;

public record DoubleOperation(double rightOperand, Operation<Double> operation) implements ContainedOperation<Double> {

    @Override
    public Double evaluate(Double leftOperand) {
        return operation.evaluate(leftOperand, rightOperand);
    }

}

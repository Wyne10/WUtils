package me.wyne.wutils.common.operation;

public class Divide<T extends Number> implements Operation<T> {
    @Override
    public T evaluate(T leftOperand, T rightOperand) {
        return Operations.getOperations(leftOperand).divide(leftOperand, rightOperand);
    }
}

package me.wyne.wutils.common.operation;

public class Multiply<T extends Number> implements Operation<T> {
    @Override
    public T evaluate(T leftOperand, T rightOperand) {
        return Operations.getOperations(leftOperand).multiply(leftOperand, rightOperand);
    }
}

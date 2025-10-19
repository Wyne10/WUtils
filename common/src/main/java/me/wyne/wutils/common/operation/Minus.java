package me.wyne.wutils.common.operation;

public class Minus<T extends Number> implements Operation<T> {
    @Override
    public T evaluate(T leftOperand, T rightOperand) {
        return Operations.getOperations(leftOperand).subtract(leftOperand, rightOperand);
    }
}

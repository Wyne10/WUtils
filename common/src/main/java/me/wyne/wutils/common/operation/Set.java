package me.wyne.wutils.common.operation;

public class Set<T extends Number> implements Operation<T> {
    @Override
    public T evaluate(T leftOperand, T rightOperand) {
        return rightOperand;
    }
}

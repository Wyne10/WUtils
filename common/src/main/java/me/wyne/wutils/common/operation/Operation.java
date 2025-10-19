package me.wyne.wutils.common.operation;

public interface Operation<T extends Number> {
    T evaluate(T leftOperand, T rightOperand);
}

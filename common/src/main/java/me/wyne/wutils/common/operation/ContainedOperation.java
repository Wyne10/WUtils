package me.wyne.wutils.common.operation;

public interface ContainedOperation<T extends Number> {
    T evaluate(T leftOperand);
}

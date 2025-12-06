package me.wyne.wutils.common.operation;

public interface ContainedOperation<T extends Number> extends Operation<T> {
    T evaluate(T leftOperand);
}

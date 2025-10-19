package me.wyne.wutils.common.operation;

public interface Operable<T extends Number> {
    T add(T first, T second);
    T subtract(T minuend, T subtrahend);
    T multiply(T multiplicand, T multiplier);
    T divide(T dividend, T divisor);
    T power(T base, T exponent);
}

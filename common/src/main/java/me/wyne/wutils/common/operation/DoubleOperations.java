package me.wyne.wutils.common.operation;

public class DoubleOperations implements Operable<Double> {
    @Override
    public Double add(Double first, Double second) {
        return first + second;
    }

    @Override
    public Double subtract(Double minuend, Double subtrahend) {
        return minuend - subtrahend;
    }

    @Override
    public Double multiply(Double multiplicand, Double multiplier) {
        return multiplicand * multiplier;
    }

    @Override
    public Double divide(Double dividend, Double divisor) {
        return dividend / divisor;
    }

    @Override
    public Double power(Double base, Double exponent) {
        return Math.pow(base, exponent);
    }
}

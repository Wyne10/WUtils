package me.wyne.wutils.common.operation;

public class IntOperations implements Operable<Integer> {
    @Override
    public Integer add(Integer first, Integer second) {
        return first + second;
    }

    @Override
    public Integer subtract(Integer minuend, Integer subtrahend) {
        return minuend - subtrahend;
    }

    @Override
    public Integer multiply(Integer multiplicand, Integer multiplier) {
        return multiplicand * multiplier;
    }

    @Override
    public Integer divide(Integer dividend, Integer divisor) {
        return dividend / divisor;
    }

    @Override
    public Integer power(Integer base, Integer exponent) {
        return (int) Math.pow(base, exponent);
    }
}

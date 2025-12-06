package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.NotNull;

public record DoubleComparator(double rightOperand, Comparator<Double> comparator) implements ContainedComparator<Double> {

    @Override
    public Boolean compare(Double leftOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

    @Override
    public Boolean compare(Comparable<Double> leftOperand, Double rightOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

    @Override
    public @NotNull String toString() {
        return Comparators.getOperator(comparator) + rightOperand;
    }

}

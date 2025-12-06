package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.NotNull;

public record IntComparator(int rightOperand, Comparator<Integer> comparator) implements ContainedComparator<Integer> {

    @Override
    public Boolean compare(Integer leftOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

    @Override
    public Boolean compare(Comparable<Integer> leftOperand, Integer rightOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

    @Override
    public @NotNull String toString() {
        return Comparators.getOperator(comparator) + rightOperand;
    }

}

package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link ContainedComparator} for {@code double} values, e.g. produced by
 * {@link Comparators#getDoubleComparator(String)} from strings like {@code ">=1.5"}.
 */
public record DoubleComparator(double rightOperand, @NotNull Comparator<Double> comparator) implements ContainedComparator<Double> {

    @Override
    public @NotNull Boolean compare(@NotNull Double leftOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

    @Override
    public @NotNull Boolean compare(@NotNull Comparable<Double> leftOperand, @NotNull Double rightOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

    @Override
    public @NotNull String toString() {
        return Comparators.getOperator(comparator) + rightOperand;
    }

}

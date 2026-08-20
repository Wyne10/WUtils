package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link ContainedComparator} for {@code int} values, e.g. produced by
 * {@link Comparators#getIntComparator(String)} from strings like {@code "<=5"}.
 */
public record IntComparator(int rightOperand, @NotNull Comparator<Integer> comparator) implements ContainedComparator<Integer> {

    @Override
    public @NotNull Boolean compare(@NotNull Integer leftOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

    @Override
    public @NotNull Boolean compare(@NotNull Comparable<Integer> leftOperand, @NotNull Integer rightOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

    @Override
    public @NotNull String toString() {
        return Comparators.getOperator(comparator) + rightOperand;
    }

}

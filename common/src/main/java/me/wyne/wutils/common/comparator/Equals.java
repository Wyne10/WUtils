package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.NotNull;

/**
 * Compares two values for equality: {@code leftOperand == rightOperand}.
 */
public class Equals<T> implements Comparator<T> {
    @Override
    public @NotNull Boolean compare(@NotNull Comparable<T> leftOperand, @NotNull T rightOperand) {
        return leftOperand.compareTo(rightOperand) == 0;
    }
}

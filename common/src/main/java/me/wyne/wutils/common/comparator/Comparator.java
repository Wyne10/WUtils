package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.NotNull;

/**
 * Strategy for comparing a value against a fixed operand, e.g. {@code <}, {@code >=}, {@code ==}.
 * <p>
 * Implementations ({@link LessThan}, {@link GreaterThan}, {@link GreaterOrEqual},
 * {@link LessOrEqual}, {@link Equals}) are stateless and delegate to
 * {@link Comparable#compareTo(Object)}. Instances are looked up by symbol via
 * {@link Comparators#getComparator(String)}.
 */
public interface Comparator<T> {
    @NotNull Boolean compare(@NotNull Comparable<T> leftOperand, @NotNull T rightOperand);
}

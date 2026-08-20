package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link Comparator} that already carries its right-hand operand, so callers only
 * need to supply the left-hand value. {@link IntComparator} and {@link DoubleComparator}
 * are the concrete implementations.
 */
public interface ContainedComparator<T> extends Comparator<T> {
    @NotNull Boolean compare(@NotNull T leftOperand);
}

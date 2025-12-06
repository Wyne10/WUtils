package me.wyne.wutils.common.comparator;

public interface ContainedComparator<T> extends Comparator<T> {
    Boolean compare(T leftOperand);
}

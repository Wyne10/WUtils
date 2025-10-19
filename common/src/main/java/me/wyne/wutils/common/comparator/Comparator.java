package me.wyne.wutils.common.comparator;

public interface Comparator<T> {
    Boolean compare(Comparable<T> leftOperand, T rightOperand);
}

package me.wyne.wutils.common.comparator;

public class LessOrEqual<T> implements Comparator<T> {
    @Override
    public Boolean compare(Comparable<T> leftOperand, T rightOperand) {
        return leftOperand.compareTo(rightOperand) <= 0;
    }
}

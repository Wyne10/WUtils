package me.wyne.wutils.common.comparator;

public record IntComparator(int rightOperand, Comparator<Integer> comparator) implements ContainedComparator<Integer> {

    @Override
    public Boolean compare(Integer leftOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

}

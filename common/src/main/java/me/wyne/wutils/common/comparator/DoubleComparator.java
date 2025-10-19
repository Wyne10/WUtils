package me.wyne.wutils.common.comparator;

public record DoubleComparator(double rightOperand, Comparator<Double> comparator) implements ContainedComparator<Double> {

    @Override
    public Boolean compare(Double leftOperand) {
        return comparator.compare(leftOperand, rightOperand);
    }

}

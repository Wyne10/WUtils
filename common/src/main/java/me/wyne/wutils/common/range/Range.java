package me.wyne.wutils.common.range;

public abstract class Range<T> implements Iterable<T> {

    private final T min;
    private final T max;

    public Range(T min, T max) {
        this.min = min;
        this.max = max;

    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public abstract T getRandom();

    public abstract boolean contains(T value);

}

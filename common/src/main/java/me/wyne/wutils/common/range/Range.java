package me.wyne.wutils.common.range;

public abstract class Range<T> implements Iterable<T> {

    private final T min;
    private final T center;
    private final T max;

    public Range(T min, T max, T center) {
        this.min = min;
        this.max = max;
        this.center = center;
    }

    public T getMin() {
        return min;
    }

    public T getCenter() {
        return center;
    }

    public T getMax() {
        return max;
    }

    public abstract T getRandom();
    public abstract boolean contains(T value);


}

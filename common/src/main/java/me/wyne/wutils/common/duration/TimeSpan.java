package me.wyne.wutils.common.duration;

public record TimeSpan(long duration, Duration type) implements Duration {
    public long getMillis() {
        return type.getMillis(duration);
    }

    public long getTicks() {
        return type.getTicks(duration);
    }

    @Override
    public long getMillis(long duration) {
        return type.getMillis(duration);
    }

    @Override
    public long getTicks(long duration) {
        return type.getTicks(duration);
    }
}

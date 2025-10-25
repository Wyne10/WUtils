package me.wyne.wutils.common.duration;

public class Ticks implements Duration {
    @Override
    public long getMillis(long duration) {
        return me.wyne.wutils.common.Ticks.toMillis(duration);
    }

    @Override
    public long getTicks(long duration) {
        return duration;
    }
}

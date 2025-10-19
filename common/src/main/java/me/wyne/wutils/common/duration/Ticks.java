package me.wyne.wutils.common.duration;

public class Ticks implements Duration {
    @Override
    public Long getMillis(Long duration) {
        return me.wyne.wutils.common.Ticks.toMillis(duration);
    }

    @Override
    public Long getTicks(Long duration) {
        return duration;
    }
}

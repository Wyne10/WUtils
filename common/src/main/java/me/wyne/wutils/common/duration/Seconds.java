package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.Ticks;

public class Seconds implements Duration {
    @Override
    public long getMillis(long duration) {
        return duration * 1000;
    }

    @Override
    public long getTicks(long duration) {
        return Ticks.ofSeconds(duration);
    }
}

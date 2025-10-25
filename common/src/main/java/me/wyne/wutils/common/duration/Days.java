package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.Ticks;

public class Days implements Duration {
    @Override
    public long getMillis(long duration) {
        return duration * 24 * 60 * 60 * 1000;
    }

    @Override
    public long getTicks(long duration) {
        return Ticks.ofSeconds(duration * 24 * 60 * 60);
    }
}

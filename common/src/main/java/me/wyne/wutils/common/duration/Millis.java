package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.Ticks;

public class Millis implements Duration {
    @Override
    public long getMillis(long duration) {
        return duration;
    }

    @Override
    public long getTicks(long duration) {
        return Ticks.ofMillis(duration);
    }
}

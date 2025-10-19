package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.Ticks;

public class Millis implements Duration {
    @Override
    public Long getMillis(Long duration) {
        return duration;
    }

    @Override
    public Long getTicks(Long duration) {
        return Ticks.ofMillis(duration);
    }
}

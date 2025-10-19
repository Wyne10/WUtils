package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.Ticks;

public class Minutes implements Duration {
    @Override
    public Long getMillis(Long duration) {
        return duration * 60 * 1000;
    }

    @Override
    public Long getTicks(Long duration) {
        return Ticks.ofSeconds(duration * 60);
    }
}

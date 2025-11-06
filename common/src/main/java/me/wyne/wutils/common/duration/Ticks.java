package me.wyne.wutils.common.duration;

import java.util.concurrent.TimeUnit;

public class Ticks implements Duration {
    @Override
    public long getMillis(long duration) {
        return me.wyne.wutils.common.Ticks.toMillis(duration);
    }

    @Override
    public long getTicks(long duration) {
        return duration;
    }

    @Override
    public long getUnit(long duration, TimeUnit unit) {
        return unit.convert(getMillis(duration), TimeUnit.MILLISECONDS);
    }
}

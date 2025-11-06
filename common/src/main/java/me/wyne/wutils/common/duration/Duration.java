package me.wyne.wutils.common.duration;

import java.util.concurrent.TimeUnit;

public interface Duration {
    long getMillis(long duration);
    long getTicks(long duration);
    long getUnit(long duration, TimeUnit unit);
}

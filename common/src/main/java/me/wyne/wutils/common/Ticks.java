package me.wyne.wutils.common;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class Ticks {

    public final static long TICKS_PER_SECOND = 20;
    public final static long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    public final static long TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;

    public static long of(long durationSeconds) {
        return durationSeconds * TICKS_PER_SECOND;
    }

    public static long of(long duration, TimeUnit unit) {
        return unit.toSeconds(duration) * TICKS_PER_SECOND;
    }

    public static Duration duration(long ticks) {
        return Duration.ofSeconds(ticks / TICKS_PER_SECOND);
    }

}

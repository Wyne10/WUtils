package me.wyne.wutils.common;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class Ticks {

    public final static long MILLIS_PER_TICK = 50;
    public final static long TICKS_PER_SECOND = 20;
    public final static long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    public final static long TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;

    public static long ofMillis(long durationMillis) {
        return durationMillis / MILLIS_PER_TICK;
    }

    public static long ofSeconds(long durationSeconds) {
        return durationSeconds * TICKS_PER_SECOND;
    }

    public static long of(long duration, TimeUnit unit) {
        return unit.toSeconds(duration) * TICKS_PER_SECOND;
    }

    public static long toMillis(long ticks) {
        return toSeconds(ticks) * 1000;
    }

    public static long toSeconds(long ticks) {
        return ticks / TICKS_PER_SECOND;
    }

    public static long to(long ticks, TimeUnit unit) {
        return unit.convert(toSeconds(ticks), TimeUnit.SECONDS);
    }

    public static Duration duration(long ticks) {
        return Duration.ofSeconds(toSeconds(ticks));
    }

}

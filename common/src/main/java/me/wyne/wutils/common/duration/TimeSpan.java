package me.wyne.wutils.common.duration;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public record TimeSpan(long duration, Duration type) implements Duration {
    public long getMillis() {
        return type.getMillis(duration);
    }

    public long getTicks() {
        return type.getTicks(duration);
    }

    public long getUnit(TimeUnit unit) {
        return unit.convert(getMillis(duration), TimeUnit.MILLISECONDS);
    }

    @Override
    public long getMillis(long duration) {
        return type.getMillis(duration);
    }

    @Override
    public long getTicks(long duration) {
        return type.getTicks(duration);
    }

    @Override
    public long getUnit(long duration, TimeUnit unit) {
        return unit.convert(getMillis(duration), TimeUnit.MILLISECONDS);
    }

    @Override
    public @NotNull String toString() {
        long millis = getMillis();

        if (millis == 0) {
            return "0t";
        }

        long days = millis / (24 * 60 * 60 * 1000);
        millis %=       24 * 60 * 60 * 1000;

        long hours = millis / (60 * 60 * 1000);
        millis %=       60 * 60 * 1000;

        long minutes = millis / (60 * 1000);
        millis %=       60 * 1000;

        long seconds = millis / 1000;
        millis %=       1000;

        StringBuilder sb = new StringBuilder();

        if (days    != 0) sb.append(days).append('d');
        if (hours   != 0) sb.append(hours).append('h');
        if (minutes != 0) sb.append(minutes).append('m');
        if (seconds != 0) sb.append(seconds).append('s');
        if (millis  != 0) sb.append(millis).append("ms");

        return sb.toString();
    }
}

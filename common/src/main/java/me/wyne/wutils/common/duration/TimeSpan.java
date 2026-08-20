package me.wyne.wutils.common.duration;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * A concrete duration: a raw {@code duration} value paired with the {@link Duration}
 * unit it is expressed in. The no-arg {@link #getMillis()}, {@link #getTicks()} and
 * {@link #getUnit(TimeUnit)} convert this record's own stored value; the inherited
 * {@link Duration} methods instead convert whatever value is passed in, using this
 * span's {@code type} as the unit (so a {@code TimeSpan} can itself be used
 * anywhere a {@link Duration} is expected).
 * <p>
 * {@link #toString()} renders ticks-typed spans as {@code "<n>t"} and every other unit
 * as a compound {@code "1d2h3m4s5ms"} string with zero components omitted, regardless
 * of the original unit.
 */
public record TimeSpan(long duration, @NotNull Duration type) implements Duration {
    public long getMillis() {
        return type.getMillis(duration);
    }

    public long getTicks() {
        return type.getTicks(duration);
    }

    public long getUnit(@NotNull TimeUnit unit) {
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
    public long getUnit(long duration, @NotNull TimeUnit unit) {
        return unit.convert(getMillis(duration), TimeUnit.MILLISECONDS);
    }

    @Override
    public @NotNull String toString() {
        if (type instanceof Ticks)
            return duration + "t";

        long millis = getMillis();

        if (millis == 0)
            return "0t";

        long days = millis / (24 * 60 * 60 * 1000);
        millis %= 24 * 60 * 60 * 1000;

        long hours = millis / (60 * 60 * 1000);
        millis %= 60 * 60 * 1000;

        long minutes = millis / (60 * 1000);
        millis %= 60 * 1000;

        long seconds = millis / 1000;
        millis %= 1000;

        StringBuilder sb = new StringBuilder();

        if (days    != 0) sb.append(days).append('d');
        if (hours   != 0) sb.append(hours).append('h');
        if (minutes != 0) sb.append(minutes).append('m');
        if (seconds != 0) sb.append(seconds).append('s');
        if (millis  != 0) sb.append(millis).append("ms");

        return sb.toString();
    }
}

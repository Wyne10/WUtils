package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.comparator.*;
import me.wyne.wutils.common.range.TimeSpanRange;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Durations {

    public static final Duration Millis = new Millis();
    public static final Duration Seconds = new Seconds();
    public static final Duration Minutes = new Minutes();
    public static final Duration Hours = new Hours();
    public static final Duration Days = new Days();
    public static final Duration Ticks = new Ticks();

    public static final Pattern DURATION_REGEX = Pattern.compile("(-?\\d+(?:\\.\\d+)?)(ms|[smhdt])", Pattern.CASE_INSENSITIVE);
    public static final Map<String, Duration> DURATION_SYMBOLS = Map.of(
            "", new Ticks(),
            "ms", new Millis(),
            "s", new Seconds(),
            "m", new Minutes(),
            "h", new Hours(),
            "d", new Days(),
            "t", new Ticks()
    );

    public static Duration getDuration(@Nullable String symbol) {
        if (symbol == null) return Ticks;
        return DURATION_SYMBOLS.get(symbol.toLowerCase());
    }

    public static TimeSpan getTimeSpan(String string) {
        return new TimeSpan(getMillis(string), Millis);
    }

    public static long getMillis(String string) {
        Matcher matcher = DURATION_REGEX.matcher(string);
        long totalMillis = 0;

        boolean found = false;
        while (matcher.find()) {
            found = true;
            long amount = Long.parseLong(matcher.group(1));
            Duration duration = getDuration(matcher.group(2));
            totalMillis += duration.getMillis(amount);
        }

        if (!found) {
            throw new IllegalArgumentException("Invalid duration: " + string);
        }

        return totalMillis;
    }

    public static long getTicks(String string) {
        return getTimeSpan(string).getTicks();
    }

    public static TimeSpanRange getTimeSpanRange(String string) {
        var split = string.split("\\.\\.");
        return new TimeSpanRange(getTimeSpan(split[0]), getTimeSpan(split[1]));
    }

}

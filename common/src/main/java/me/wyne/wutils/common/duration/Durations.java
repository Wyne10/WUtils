package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.comparator.*;
import me.wyne.wutils.common.range.TimeSpanRange;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Durations {

    public static final Duration Millis = new Millis();
    public static final Duration Seconds = new Seconds();
    public static final Duration Minutes = new Minutes();
    public static final Duration Hours = new Hours();
    public static final Duration Days = new Days();
    public static final Duration Ticks = new Ticks();

    public static final Pattern DURATION_REGEX = Pattern.compile("(-?\\d+(?:\\.\\d+)?)(ms|[smhdt])", Pattern.CASE_INSENSITIVE);
    public static final Map<String, Duration> SYMBOL_TO_DURATION = Map.of(
            "", Ticks,
            "ms", Millis,
            "s", Seconds,
            "m", Minutes,
            "h", Hours,
            "d", Days,
            "t", Ticks
    );
    public static final Map<Duration, String> DURATION_TO_SYMBOL = SYMBOL_TO_DURATION
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static Duration getDuration(@Nullable String symbol) {
        if (symbol == null) return Ticks;
        return SYMBOL_TO_DURATION.get(symbol.toLowerCase());
    }

    public static String getSymbol(@Nullable Duration duration) {
        if (duration == null) return "";
        return DURATION_TO_SYMBOL.get(duration);
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

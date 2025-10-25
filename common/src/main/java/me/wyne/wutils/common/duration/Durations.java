package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.comparator.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Durations {

    public static final Pattern DURATION_REGEX = Pattern.compile("(-?\\d+(?:\\.\\d+)?)(ms|[smhdt])?");
    public static final Map<String, Duration> DURATION_SYMBOLS = Map.of(
            "", new Millis(),
            "ms", new Millis(),
            "s", new Seconds(),
            "m", new Minutes(),
            "h", new Hours(),
            "d", new Days(),
            "t", new Ticks()
    );

    public static Duration getDuration(@Nullable String symbol) {
        if (symbol == null) DURATION_SYMBOLS.get("ms");
        return DURATION_SYMBOLS.get(symbol);
    }

    public static TimeSpan getTimeSpan(String string) {
        Matcher matcher = DURATION_REGEX.matcher(string);
        String duration = matcher.group(1);
        String type = matcher.group(2);
        return new TimeSpan(Long.parseLong(duration), getDuration(type));
    }

    public static long getMillis(String string) {
        Matcher matcher = DURATION_REGEX.matcher(string);
        String duration = matcher.group(1);
        String type = matcher.group(2);
        return getDuration(type).getMillis(Long.parseLong(duration));
    }

    public static long getTicks(String string) {
        Matcher matcher = DURATION_REGEX.matcher(string);
        String duration = matcher.group(1);
        String type = matcher.group(2);
        return getDuration(type).getTicks(Long.parseLong(duration));
    }

}

package me.wyne.wutils.common.duration;

import me.wyne.wutils.common.comparator.*;
import me.wyne.wutils.common.range.TimeSpanRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Registry of the built-in {@link Duration} units and parser for duration strings such
 * as {@code "1d2h30m"} or {@code "50t"}.
 */
public final class Durations {

    public static final @NotNull Duration Millis = new Millis();
    public static final @NotNull Duration Seconds = new Seconds();
    public static final @NotNull Duration Minutes = new Minutes();
    public static final @NotNull Duration Hours = new Hours();
    public static final @NotNull Duration Days = new Days();
    public static final @NotNull Duration Ticks = new Ticks();

    public static final @NotNull Pattern DURATION_REGEX = Pattern.compile("(\\d+)(ms|[smhdt])?", Pattern.CASE_INSENSITIVE);
    public static final @NotNull Map<@NotNull String, @NotNull Duration> SYMBOL_TO_DURATION = Map.of(
            "ms", Millis,
            "s", Seconds,
            "m", Minutes,
            "h", Hours,
            "d", Days,
            "t", Ticks
    );
    public static final @NotNull Map<@NotNull Duration, @NotNull String> DURATION_TO_SYMBOL = SYMBOL_TO_DURATION
            .entrySet()
            .stream()
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getValue, Map.Entry::getKey));

    /**
     * Resolves a unit by its symbol ({@code ms}, {@code s}, {@code m}, {@code h},
     * {@code d}, {@code t}; case-insensitive). A {@code null} or blank symbol defaults
     * to {@link #Ticks}, but a non-blank symbol that isn't one of the six above yields
     * {@code null} rather than a default or an exception.
     */
    public static @Nullable Duration getDuration(@Nullable String symbol) {
        if (symbol == null || symbol.isBlank()) return Ticks;
        return SYMBOL_TO_DURATION.get(symbol.toLowerCase());
    }

    /**
     * Returns the symbol for one of the built-in unit instances. A {@code null}
     * duration returns {@code "t"}, but a {@link Duration} that isn't one of the six
     * built-in instances yields {@code null} rather than a default.
     */
    public static @Nullable String getSymbol(@Nullable Duration duration) {
        if (duration == null) return "t";
        return DURATION_TO_SYMBOL.get(duration);
    }

    /**
     * Parses a compound duration string (e.g. {@code "1d2h30m"}) into a
     * {@link TimeSpan} expressed in {@link #Millis}.
     *
     * @throws IllegalArgumentException if {@code string} contains no recognizable duration token
     */
    public static @NotNull TimeSpan getTimeSpan(@NotNull String string) {
        return new TimeSpan(getMillis(string), Millis);
    }

    /**
     * Sums every {@code <amount><unit>} token found anywhere in {@code string} into a
     * total in milliseconds. Matching uses {@link Matcher#find()}, not a full-string
     * match, so unrecognized characters between tokens are silently ignored as long as
     * at least one valid token is present. A bare number with no unit is treated as
     * ticks. Negative amounts are treated as their absolute value.
     *
     * @throws IllegalArgumentException if {@code string} contains no recognizable duration token
     */
    public static long getMillis(@NotNull String string) {
        Matcher matcher = DURATION_REGEX.matcher(string);
        long totalMillis = 0;

        boolean found = false;
        while (matcher.find()) {
            found = true;
            long amount = Math.abs(Long.parseLong(matcher.group(1)));
            Duration duration = getDuration(matcher.group(2));
            totalMillis += duration.getMillis(amount);
        }

        if (!found) {
            throw new IllegalArgumentException("Invalid duration: " + string);
        }

        return totalMillis;
    }

    /**
     * Parses a compound duration string (e.g. {@code "1d2h30m"}) into ticks; see
     * {@link #getMillis(String)}.
     */
    public static long getTicks(@NotNull String string) {
        return getTimeSpan(string).getTicks();
    }

    /**
     * Parses a {@code "<from>..<to>"} range of duration strings into a
     * {@link TimeSpanRange}.
     */
    public static @NotNull TimeSpanRange getTimeSpanRange(@NotNull String string) {
        return TimeSpanRange.getTimeSpanRange(string);
    }

}

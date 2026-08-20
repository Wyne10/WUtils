package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Looks up {@link Comparator} instances by their textual operator symbol and parses
 * {@code "<operator><number>"} expressions (e.g. {@code ">=5"}, {@code "3.5"}) into
 * {@link IntComparator} / {@link DoubleComparator} instances.
 */
public final class Comparators {

    public static final @NotNull Pattern COMPARATOR_REGEX = Pattern.compile("(<=|>=|==|<|>)?(-?\\d+(?:\\.\\d+)?)");

    /**
     * Resolves a comparator by its symbol ({@code <}, {@code >}, {@code <=}, {@code >=}).
     * Any other symbol, including {@code null} or {@code "=="}, resolves to {@link Equals}.
     */
    public static <T> @NotNull Comparator<T> getComparator(@Nullable String operator) {
        if (operator == null) return new Equals<>();
        return switch (operator) {
            case "<" -> new LessThan<>();
            case ">" -> new GreaterThan<>();
            case "<=" -> new LessOrEqual<>();
            case ">=" -> new GreaterOrEqual<>();
            default -> new Equals<>();
        };
    }

    /**
     * Returns the textual symbol for a comparator instance. {@code null} and any
     * comparator without a dedicated symbol (e.g. {@link Equals}) return {@code ""}.
     */
    public static <T> @NotNull String getOperator(@Nullable Comparator<T> comparator) {
        if (comparator == null) return "";
        if (comparator instanceof LessThan<T>) return "<";
        else if (comparator instanceof GreaterThan<T>) return ">";
        else if (comparator instanceof LessOrEqual<T>) return "<=";
        else if (comparator instanceof GreaterOrEqual<T>) return ">=";
        else return "";
    }

    /**
     * Parses a string of the form {@code "<operator><integer>"} (e.g. {@code "<=5"})
     * into an {@link IntComparator}. The operator is optional and defaults to {@code ==}.
     *
     * @throws IllegalStateException if {@code string} does not match {@link #COMPARATOR_REGEX}
     */
    public static @NotNull IntComparator getIntComparator(@NotNull String string) {
        Matcher matcher = COMPARATOR_REGEX.matcher(string);
        matcher.matches();
        String operator = matcher.group(1);
        String number = matcher.group(2);
        return new IntComparator(Integer.parseInt(number), getComparator(operator));
    }

    /**
     * Parses a string of the form {@code "<operator><number>"} (e.g. {@code ">=1.5"})
     * into a {@link DoubleComparator}. The operator is optional and defaults to {@code ==}.
     *
     * @throws IllegalStateException if {@code string} does not match {@link #COMPARATOR_REGEX}
     */
    public static @NotNull DoubleComparator getDoubleComparator(@NotNull String string) {
        Matcher matcher = COMPARATOR_REGEX.matcher(string);
        matcher.matches();
        String operator = matcher.group(1);
        String number = matcher.group(2);
        return new DoubleComparator(Double.parseDouble(number), getComparator(operator));
    }

}

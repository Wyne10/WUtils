package me.wyne.wutils.common.comparator;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Comparators {

    public static final Pattern COMPARATOR_REGEX = Pattern.compile("(<=|>=|==|<|>)?(-?\\d+(?:\\.\\d+)?)");

    public static <T> Comparator<T> getComparator(@Nullable String operator) {
        if (operator == null) return new Equals<>();
        return switch (operator) {
            case "<" -> new LessThan<>();
            case ">" -> new GreaterThan<>();
            case "<=" -> new LessOrEqual<>();
            case ">=" -> new GreaterOrEqual<>();
            default -> new Equals<>();
        };
    }

    public static IntComparator getIntComparator(String string) {
        Matcher matcher = COMPARATOR_REGEX.matcher(string);
        matcher.matches();
        String operator = matcher.group(1);
        String number = matcher.group(2);
        return new IntComparator(Integer.parseInt(number), getComparator(operator));
    }

    public static DoubleComparator getDoubleComparator(String string) {
        Matcher matcher = COMPARATOR_REGEX.matcher(string);
        matcher.matches();
        String operator = matcher.group(1);
        String number = matcher.group(2);
        return new DoubleComparator(Double.parseDouble(number), getComparator(operator));
    }

}

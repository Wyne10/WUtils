package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Operations {

    public static final Pattern OPERATION_REGEX = Pattern.compile("(\\+|-|\\*|/|\\*\\*)?(-?\\d+(?:\\.\\d+)?)");
    public static final IntOperations INT_OPERATIONS = new IntOperations();
    public static final DoubleOperations DOUBLE_OPERATIONS = new DoubleOperations();

    public static <T extends Number> Operable<T> getOperations(T number) {
        if (number instanceof Integer)
            return (Operable<T>) INT_OPERATIONS;
        else if (number instanceof Double)
            return (Operable<T>) DOUBLE_OPERATIONS;
        else
            throw new IllegalArgumentException("Unknown operable type");
    }

    public static <T extends Number> Operation<T> getOperation(@Nullable String operator) {
        if (operator == null) return new Set<>();
        return switch (operator) {
            case "+" -> new Plus<>();
            case "-" -> new Minus<>();
            case "*" -> new Multiply<>();
            case "/" -> new Divide<>();
            case "**" -> new Power<>();
            default -> new Set<>();
        };
    }

    public static IntOperation getIntOperation(String string) {
        Matcher matcher = OPERATION_REGEX.matcher(string);
        matcher.matches();
        String operator = matcher.group(1);
        String number = matcher.group(2);
        return new IntOperation(Integer.parseInt(number), getOperation(operator));
    }

    public static DoubleOperation getDoubleOperation(String string) {
        Matcher matcher = OPERATION_REGEX.matcher(string);
        matcher.matches();
        String operator = matcher.group(1);
        String number = matcher.group(2);
        return new DoubleOperation(Double.parseDouble(number), getOperation(operator));
    }

}

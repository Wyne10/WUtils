package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves {@link Operable} implementations by runtime {@link Number} type, looks up
 * {@link Operation} instances by their textual operator symbol, and parses
 * {@code "<operator><number>"} expressions (e.g. {@code "+5"}, {@code "**2"}) into
 * {@link IntOperation} / {@link DoubleOperation} instances.
 */
public final class Operations {

    public static final @NotNull Pattern OPERATION_REGEX = Pattern.compile("(\\+|-|\\*|/|\\*\\*)?(-?\\d+(?:\\.\\d+)?)");
    public static final @NotNull IntOperations INT_OPERATIONS = new IntOperations();
    public static final @NotNull DoubleOperations DOUBLE_OPERATIONS = new DoubleOperations();

    /**
     * Returns the {@link Operable} matching the runtime type of {@code number}.
     * Only {@link Integer} and {@link Double} are supported.
     *
     * @throws IllegalArgumentException if {@code number} is any other {@link Number} type (or {@code null})
     */
    public static <T extends Number> @NotNull Operable<T> getOperations(@NotNull T number) {
        if (number instanceof Integer)
            return (Operable<T>) INT_OPERATIONS;
        else if (number instanceof Double)
            return (Operable<T>) DOUBLE_OPERATIONS;
        else
            throw new IllegalArgumentException("Unknown operable type");
    }

    /**
     * Resolves an operation by its symbol ({@code +}, {@code -}, {@code *}, {@code /},
     * {@code **}). Any other symbol, including {@code null}, resolves to {@link Set}.
     */
    public static <T extends Number> @NotNull Operation<T> getOperation(@Nullable String operator) {
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

    /**
     * Returns the textual symbol for an operation instance. {@code null} and any
     * operation without a dedicated symbol (e.g. {@link Set}) return {@code ""}.
     */
    public static <T extends Number> @NotNull String getOperator(@Nullable Operation<T> operation) {
        if (operation == null) return "";
        if (operation instanceof Plus) return "+";
        else if (operation instanceof Minus) return "-";
        else if (operation instanceof Multiply) return "*";
        else if (operation instanceof Divide) return "/";
        else if (operation instanceof Power) return "**";
        else return "";
    }

    /**
     * Parses a string of the form {@code "<operator><integer>"} (e.g. {@code "+5"})
     * into an {@link IntOperation}. The operator is optional and defaults to {@link Set}.
     *
     * @throws IllegalStateException if {@code string} does not match {@link #OPERATION_REGEX}
     */
    public static @NotNull IntOperation getIntOperation(@NotNull String string) {
        Matcher matcher = OPERATION_REGEX.matcher(string);
        matcher.matches();
        String operator = matcher.group(1);
        String number = matcher.group(2);
        return new IntOperation(Integer.parseInt(number), getOperation(operator));
    }

    /**
     * Parses a string of the form {@code "<operator><number>"} (e.g. {@code "*1.5"})
     * into a {@link DoubleOperation}. The operator is optional and defaults to {@link Set}.
     *
     * @throws IllegalStateException if {@code string} does not match {@link #OPERATION_REGEX}
     */
    public static @NotNull DoubleOperation getDoubleOperation(@NotNull String string) {
        Matcher matcher = OPERATION_REGEX.matcher(string);
        matcher.matches();
        String operator = matcher.group(1);
        String number = matcher.group(2);
        return new DoubleOperation(Double.parseDouble(number), getOperation(operator));
    }

}

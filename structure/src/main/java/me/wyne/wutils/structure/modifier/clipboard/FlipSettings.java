package me.wyne.wutils.structure.modifier.clipboard;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The set of flip axes one of which is picked at random for each placement.
 */
public record FlipSettings(@NotNull List<Axis> axes) {

    public enum Axis {
        NONE, X, Y, Z
    }

    private static final List<Axis> DEFAULT = List.of(Axis.NONE, Axis.X, Axis.Z);

    /**
     * Parses a whitespace-separated list of flip axis tokens.
     *
     * <p>An empty string, {@code true}, {@code random} or {@code any} (case-insensitive) yields
     * the default set {@code none x z}. Each token accepts the axis letter or a compass synonym:
     * {@code west}/{@code east} for X, {@code up}/{@code down} for Y, {@code north}/{@code south}
     * for Z, and {@code none}/{@code 0}/{@code -} for no flip.</p>
     *
     * @throws IllegalArgumentException if a token matches none of the known axes
     */
    public static @NotNull FlipSettings parse(@NotNull String input) {
        var trimmed = input.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("true")
                || trimmed.equalsIgnoreCase("random") || trimmed.equalsIgnoreCase("any"))
            return new FlipSettings(DEFAULT);

        List<Axis> parsed = new ArrayList<>();
        for (String token : new Args(input, Args.SPACE_DELIMITER).getArgs()) {
            if (token.isBlank())
                continue;
            parsed.add(parseAxis(token.trim()));
        }
        return new FlipSettings(parsed.isEmpty() ? DEFAULT : List.copyOf(parsed));
    }

    private static @NotNull Axis parseAxis(@NotNull String token) {
        return switch (token.toLowerCase()) {
            case "x", "west", "east" -> Axis.X;
            case "y", "up", "down" -> Axis.Y;
            case "z", "north", "south" -> Axis.Z;
            case "none", "0", "-" -> Axis.NONE;
            default -> throw new IllegalArgumentException("Unknown flip axis: '" + token + "'");
        };
    }

    @Override
    public @NotNull String toString() {
        return axes.stream().map(axis -> axis.name().toLowerCase()).collect(Collectors.joining(" "));
    }
}

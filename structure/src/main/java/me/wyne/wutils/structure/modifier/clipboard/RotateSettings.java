package me.wyne.wutils.structure.modifier.clipboard;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record RotateSettings(@NotNull List<Integer> angles) {

    private static final List<Integer> DEFAULT = List.of(0, 90, 180, 270);

    public static @NotNull RotateSettings parse(@NotNull String input) {
        var trimmed = input.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("true")
                || trimmed.equalsIgnoreCase("random") || trimmed.equalsIgnoreCase("any"))
            return new RotateSettings(DEFAULT);

        List<Integer> parsed = new ArrayList<>();
        for (String token : new Args(input, Args.SPACE_DELIMITER).getArgs()) {
            if (token.isBlank())
                continue;
            int degrees;
            try {
                degrees = Integer.parseInt(token.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Rotation angle must be an integer: '" + token + "'");
            }
            if (degrees % 90 != 0)
                throw new IllegalArgumentException("Rotation angle must be a multiple of 90: '" + token + "'");
            parsed.add(((degrees % 360) + 360) % 360);
        }
        return new RotateSettings(parsed.isEmpty() ? DEFAULT : List.copyOf(parsed));
    }

    @Override
    public @NotNull String toString() {
        return angles.stream().map(String::valueOf).collect(Collectors.joining(" "));
    }
}

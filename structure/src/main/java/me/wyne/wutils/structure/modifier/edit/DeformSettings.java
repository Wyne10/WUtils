package me.wyne.wutils.structure.modifier.edit;

import org.jetbrains.annotations.NotNull;

public record DeformSettings(int margin, @NotNull String expression) {

    public static @NotNull DeformSettings parse(@NotNull String input) {
        String[] parts = input.trim().split("\\s+", 2);
        int margin = Integer.parseInt(parts[0]);
        String expression = parts.length > 1 ? parts[1] : "";
        return new DeformSettings(margin, expression);
    }

    @Override
    public @NotNull String toString() {
        return margin + " " + expression;
    }
}

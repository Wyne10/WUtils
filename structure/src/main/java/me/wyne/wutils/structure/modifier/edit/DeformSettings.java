package me.wyne.wutils.structure.modifier.edit;

import org.jetbrains.annotations.NotNull;

/**
 * Config value for {@link DeformEditModifier}, parsed by {@link #parse} from a single string:
 * {@code margin expression}, split on the <em>first</em> run of whitespace only, so
 * {@code expression} — a WorldEdit deform expression — may itself contain spaces. Unlike its
 * sibling settings, {@code margin} has no default and empty input throws
 * {@link NumberFormatException}.
 */
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

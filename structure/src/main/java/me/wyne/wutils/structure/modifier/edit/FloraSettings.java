package me.wyne.wutils.structure.modifier.edit;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

/**
 * Config value for {@link FloraEditModifier}, parsed by {@link #parse} from a single string:
 * {@code margin(0) density(5) includeClipboard(false)}. {@code density} is a percent chance per
 * column. {@code includeClipboard} controls whether growth is allowed on the structure's own
 * footprint; when {@code false} the footprint is excluded.
 *
 * <p>Unlike most of its siblings, tokens here are split with {@link Args}'s default
 * colon-or-whitespace delimiter rather than {@link Args#SPACE_DELIMITER}, so a value containing
 * a colon is split there too.</p>
 */
public record FloraSettings(int margin, double density, boolean includeClipboard) {

    public static @NotNull FloraSettings parse(@NotNull String input) {
        var args = new Args(input);
        int margin = Integer.parseInt(args.get(0, "0"));
        double density = Double.parseDouble(args.get(1, "5"));
        boolean includeClipboard = Boolean.parseBoolean(args.get(2, "false"));
        return new FloraSettings(margin, density, includeClipboard);
    }

    @Override
    public @NotNull String toString() {
        return margin + " " + density + " " + includeClipboard;
    }
}

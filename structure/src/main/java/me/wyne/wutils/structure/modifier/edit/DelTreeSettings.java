package me.wyne.wutils.structure.modifier.edit;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

/**
 * Config value for {@link DelTreeEditModifier}, parsed by {@link #parse} from a single
 * space-delimited string: {@code margin(0) includeClipboard(false)}.
 * {@code includeClipboard} controls whether the removal is allowed to touch the structure's own
 * footprint; when {@code false} the footprint is excluded from the scan.
 */
public record DelTreeSettings(int margin, boolean includeClipboard) {

    public static @NotNull DelTreeSettings parse(@NotNull String input) {
        var args = new Args(input, Args.SPACE_DELIMITER);
        int margin = Integer.parseInt(args.get(0, "0"));
        boolean includeClipboard = Boolean.parseBoolean(args.get(1, "false"));
        return new DelTreeSettings(margin, includeClipboard);
    }

    @Override
    public @NotNull String toString() {
        return margin + " " + includeClipboard;
    }
}

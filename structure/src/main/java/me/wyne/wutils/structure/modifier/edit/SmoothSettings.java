package me.wyne.wutils.structure.modifier.edit;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Config value for {@link SmoothEditModifier}, parsed by {@link #parse} from a single
 * space-delimited string: {@code margin(5) iterations(1) [mask]}.
 */
public record SmoothSettings(int margin, int iterations, @Nullable String mask) {

    public static @NotNull SmoothSettings parse(@NotNull String input) {
        var args = new Args(input, Args.SPACE_DELIMITER);
        int margin = Integer.parseInt(args.get(0, "5"));
        int iterations = Integer.parseInt(args.get(1, "1"));
        String mask = args.getNullable(2);
        return new SmoothSettings(margin, iterations, mask);
    }

    @Override
    public @NotNull String toString() {
        String base = margin + " " + iterations;
        return mask == null ? base : base + " " + mask;
    }
}

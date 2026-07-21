package me.wyne.wutils.structure.modifier.edit;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SmoothSettings(int iterations, int margin, @Nullable String mask) {

    public static @NotNull SmoothSettings parse(@NotNull String input) {
        var args = new Args(input);
        int iterations = Integer.parseInt(args.get(0, "1"));
        int margin = Integer.parseInt(args.get(1, "5"));
        String mask = args.getNullable(2);
        return new SmoothSettings(iterations, margin, mask);
    }

    @Override
    public @NotNull String toString() {
        String base = iterations + " " + margin;
        return mask == null ? base : base + " " + mask;
    }
}

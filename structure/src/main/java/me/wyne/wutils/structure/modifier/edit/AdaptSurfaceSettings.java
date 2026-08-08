package me.wyne.wutils.structure.modifier.edit;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

public record AdaptSurfaceSettings(int margin, @NotNull String mask, @NotNull String sampleMask) {

    public static @NotNull AdaptSurfaceSettings parse(@NotNull String input) {
        var args = new Args(input, Args.SPACE_DELIMITER);
        int margin = Integer.parseInt(args.get(0, "4"));
        String mask = args.get(1, "");
        String sampleMask = args.get(2, "#surface");
        return new AdaptSurfaceSettings(margin, mask, sampleMask);
    }

    @Override
    public @NotNull String toString() {
        return margin + " " + mask + " " + sampleMask;
    }
}

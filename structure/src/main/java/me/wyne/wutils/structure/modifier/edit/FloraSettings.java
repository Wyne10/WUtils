package me.wyne.wutils.structure.modifier.edit;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

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

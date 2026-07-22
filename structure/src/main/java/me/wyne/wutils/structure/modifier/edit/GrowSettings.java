package me.wyne.wutils.structure.modifier.edit;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.operation.IntOperation;
import me.wyne.wutils.common.operation.Operations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GrowSettings(int margin, double strength, @NotNull IntOperation base, @Nullable String mask) {

    public static @NotNull GrowSettings parse(@NotNull String input) {
        var args = new Args(input, Args.SPACE_DELIMITER);
        int margin = Integer.parseInt(args.get(0, "5"));
        double strength = Double.parseDouble(args.get(1, "2"));
        IntOperation base = Operations.getIntOperation(args.get(2, "+0"));
        String mask = args.getNullable(3);
        return new GrowSettings(margin, strength, base, mask);
    }

    @Override
    public @NotNull String toString() {
        String result = margin + " " + strength + " " + base;
        return mask == null ? result : result + " " + mask;
    }
}

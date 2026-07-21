package me.wyne.wutils.structure.modifier.region;

import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

public record ScopedAmount(int amount, boolean horizontal, boolean vertical) {

    public static @NotNull ScopedAmount parse(@NotNull String input) {
        boolean h = false, v = false;
        int amount = 1;
        for (String token : new Args(input).getArgs()) {
            switch (token.toLowerCase()) {
                case "-h" -> h = true;
                case "-v" -> v = true;
                default -> amount = Integer.parseInt(token);
            }
        }
        if (h == v)
            return new ScopedAmount(amount, true, true);
        return new ScopedAmount(amount, h, v);
    }

    public int horizontalAmount() {
        return horizontal ? amount : 0;
    }

    public int verticalAmount() {
        return vertical ? amount : 0;
    }

    @Override
    public @NotNull String toString() {
        if (horizontal && !vertical) return amount + " -h";
        if (vertical && !horizontal) return amount + " -v";
        return String.valueOf(amount);
    }
}
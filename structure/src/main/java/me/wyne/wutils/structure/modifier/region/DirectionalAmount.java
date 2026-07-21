package me.wyne.wutils.structure.modifier.region;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

public record DirectionalAmount(@NotNull Direction direction, int amount) {

    public static @NotNull DirectionalAmount parse(@NotNull String input) {
        var args = new Args(input);
        Direction direction = Direction.valueOf(args.get(0).toUpperCase());
        int amount = Integer.parseInt(args.get(1, "1"));
        return new DirectionalAmount(direction, amount);
    }

    public @NotNull BlockVector3 delta() {
        return direction.toBlockVector().multiply(amount);
    }

    @Override
    public @NotNull String toString() {
        return direction.name().toLowerCase() + " " + amount;
    }

}
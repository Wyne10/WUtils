package me.wyne.wutils.structure.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public record LazyMask(@NotNull String mask) {

    public Mask getMask() {
        return MaskUtils.parseMask(mask);
    }

    public Mask getMask(@NotNull World world) {
        return MaskUtils.parseMask(mask, world);
    }

    public Mask getMask(@NotNull com.sk89q.worldedit.world.World world) {
        return MaskUtils.parseMask(mask, world);
    }

    public Mask getMask(@NotNull Extent extent) {
        return MaskUtils.parseMask(mask, extent);
    }

    @Override
    public @NotNull String toString() {
        return mask;
    }

}

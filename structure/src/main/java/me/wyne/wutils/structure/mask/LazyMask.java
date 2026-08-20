package me.wyne.wutils.structure.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * A mask kept as an unparsed string, requires WorldEdit on the classpath.
 *
 * <p>The string is parsed on every {@code getMask} call rather than once up front, so the same
 * mask can be resolved later against whichever world or extent context is in play at the point
 * of use.</p>
 */
public record LazyMask(@NotNull String mask) {

    public @NotNull Mask getMask() {
        return MaskUtils.parseMask(mask);
    }

    public @NotNull Mask getMask(@NotNull World world) {
        return MaskUtils.parseMask(mask, world);
    }

    public @NotNull Mask getMask(@NotNull com.sk89q.worldedit.world.World world) {
        return MaskUtils.parseMask(mask, world);
    }

    public @NotNull Mask getMask(@NotNull Extent extent) {
        return MaskUtils.parseMask(mask, extent);
    }

    public @NotNull Mask getMask(@NotNull com.sk89q.worldedit.world.World world, @NotNull Extent extent) {
        return MaskUtils.parseMask(mask, world, extent);
    }

    @Override
    public @NotNull String toString() {
        return mask;
    }

}

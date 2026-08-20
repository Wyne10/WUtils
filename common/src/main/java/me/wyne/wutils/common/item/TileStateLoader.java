package me.wyne.wutils.common.item;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Persists a tile entity's type-specific state (beyond what {@link org.bukkit.inventory.meta.BlockStateMeta}
 * captures on its own) between a {@link BlockState} and an {@link ItemStack}. Registered per
 * block {@link org.bukkit.Material} in {@link ItemUtils#TILE_STATE_LOADERS}.
 */
public interface TileStateLoader {

    /**
     * Writes {@code blockState}'s extra state onto {@code item} (typically via
     * {@link ItemStack#editMeta}) and returns it.
     */
    @NotNull ItemStack save(@NotNull ItemStack item, @NotNull BlockState blockState);

    /** Applies extra state saved on {@code item} onto {@code blockState} and returns it. */
    @NotNull BlockState load(@NotNull ItemStack item, @NotNull BlockState blockState);
}

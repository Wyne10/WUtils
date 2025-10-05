package me.wyne.wutils.common.item;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public interface TileStateLoader {
    ItemStack save(ItemStack item, BlockState blockState);
    BlockState load(ItemStack item, BlockState blockState);
}

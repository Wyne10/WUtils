package me.wyne.wutils.common.item;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public interface BlockStateMetaExtension {
    ItemStack extend(ItemStack item, BlockState blockState);
}

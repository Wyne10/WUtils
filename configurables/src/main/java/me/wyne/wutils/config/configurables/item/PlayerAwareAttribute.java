package me.wyne.wutils.config.configurables.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PlayerAwareAttribute {
    void apply(ItemStack item, Player player);
}

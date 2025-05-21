package me.wyne.wutils.config.configurables.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PlayerAwareAttribute<V> extends Attribute<V> {
    void apply(ItemStack item, Player player);
}

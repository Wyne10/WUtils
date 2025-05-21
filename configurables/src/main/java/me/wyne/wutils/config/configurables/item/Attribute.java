package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;

public interface Attribute<V> {
    String getKey();
    V getValue();
    void apply(ItemStack item);
    default String toConfig() {
        return getKey() + ": " + toString();
    }
}

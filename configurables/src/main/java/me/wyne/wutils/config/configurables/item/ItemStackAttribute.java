package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;

public interface ItemStackAttribute {
    default void apply(ItemStack item) {
        apply(item, ItemAttributeContext.EMPTY);
    }

    default void apply(ItemStack item, ItemAttributeContext context) {
        apply(item);
    }
}

package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;

public interface ContextItemStackAttribute extends ItemStackAttribute {
    default void apply(ItemStack item) {
        apply(item, ItemAttributeContext.EMPTY);
    }

    void apply(ItemStack item, ItemAttributeContext context);
}

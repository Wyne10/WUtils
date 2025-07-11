package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface MetaAttribute extends ItemStackAttribute {

    default void apply(ItemStack item) {
        item.editMeta(this::apply);
    }

    default void apply(ItemStack item, ItemAttributeContext context) {
        item.editMeta(meta -> apply(meta, context));
    }

    default void apply(ItemMeta meta) {
        apply(meta, ItemAttributeContext.EMPTY);
    }

    default void apply(ItemMeta meta, ItemAttributeContext context) {
        apply(meta);
    }

}

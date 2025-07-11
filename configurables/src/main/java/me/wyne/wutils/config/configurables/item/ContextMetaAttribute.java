package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface ContextMetaAttribute extends ContextItemStackAttribute, MetaAttribute {
    @Override
    default void apply(ItemStack item) {
        item.editMeta(this::apply);
    }

    @Override
    default void apply(ItemStack item, ItemAttributeContext context) {
        item.editMeta(meta -> apply(meta, context));
    }

    @Override
    default void apply(ItemMeta meta) {
        apply(meta, ItemAttributeContext.EMPTY);
    }

    void apply(ItemMeta meta, ItemAttributeContext context);
}

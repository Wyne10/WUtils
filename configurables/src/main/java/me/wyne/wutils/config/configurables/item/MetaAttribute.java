package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface MetaAttribute extends ItemStackAttribute {

    default void apply(ItemStack item) {
        item.editMeta(this::apply);
    }

    void apply(ItemMeta meta);

}

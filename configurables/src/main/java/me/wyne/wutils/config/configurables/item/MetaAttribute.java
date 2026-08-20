package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link ItemStackAttribute} that only needs the item's {@link ItemMeta}.
 *
 * <p>The default {@link #apply(ItemStack)} routes through {@link ItemStack#editMeta}, so most
 * attributes only need to implement {@link #apply(ItemMeta)}.</p>
 */
public interface MetaAttribute extends ItemStackAttribute {
    default void apply(@NotNull ItemStack item) {
        item.editMeta(this::apply);
    }

    void apply(@NotNull ItemMeta meta);
}

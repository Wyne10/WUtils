package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Combines {@link ContextItemStackAttribute} and {@link MetaAttribute}: an attribute that needs
 * both the item's meta and the {@link ItemAttributeContext}. Used by {@code name}, {@code lore}
 * and {@code skullPlayer}.
 */
public interface ContextMetaAttribute extends ContextItemStackAttribute, MetaAttribute {
    @Override
    default void apply(@NotNull ItemStack item) {
        item.editMeta(this::apply);
    }

    @Override
    default void apply(@NotNull ItemStack item, @NotNull ItemAttributeContext context) {
        item.editMeta(meta -> apply(meta, context));
    }

    @Override
    default void apply(@NotNull ItemMeta meta) {
        apply(meta, ItemAttributeContext.EMPTY);
    }

    void apply(@NotNull ItemMeta meta, @NotNull ItemAttributeContext context);
}

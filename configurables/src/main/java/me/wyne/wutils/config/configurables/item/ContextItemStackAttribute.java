package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link ItemStackAttribute} that additionally needs an {@link ItemAttributeContext} — the
 * player and replacements to resolve against, typically for i18n.
 *
 * <p>{@code ItemConfigurable.build} prefers this interface over the plain
 * {@link ItemStackAttribute#apply(ItemStack)} when both are implemented, passing the build's
 * context through.</p>
 */
public interface ContextItemStackAttribute extends ItemStackAttribute {
    default void apply(@NotNull ItemStack item) {
        apply(item, ItemAttributeContext.EMPTY);
    }

    void apply(@NotNull ItemStack item, @NotNull ItemAttributeContext context);
}

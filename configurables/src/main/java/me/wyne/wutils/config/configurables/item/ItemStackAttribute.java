package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Applies one configured aspect of an item directly to an {@link ItemStack}.
 *
 * <p>{@link me.wyne.wutils.config.configurables.ItemConfigurable} applies every registered
 * attribute implementing this interface, in registration order, mutating the stack in place.</p>
 */
public interface ItemStackAttribute {
    void apply(@NotNull ItemStack item);
}

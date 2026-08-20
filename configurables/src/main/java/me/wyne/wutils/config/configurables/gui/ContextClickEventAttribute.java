package me.wyne.wutils.config.configurables.gui;

import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ClickEventAttribute} that additionally needs the {@link ItemAttributeContext} the item
 * was built with, typically for i18n.
 */
public interface ContextClickEventAttribute extends ClickEventAttribute {
    default void apply(@NotNull InventoryClickEvent event) {
        apply(event, ItemAttributeContext.EMPTY);
    }

    void apply(@NotNull InventoryClickEvent event, @NotNull ItemAttributeContext context);
}

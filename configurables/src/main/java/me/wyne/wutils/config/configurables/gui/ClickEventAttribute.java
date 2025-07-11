package me.wyne.wutils.config.configurables.gui;

import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickEventAttribute {
    default void apply(InventoryClickEvent event) {
        apply(event, ItemAttributeContext.EMPTY);
    }

    default void apply(InventoryClickEvent event, ItemAttributeContext context) {
        apply(event);
    }
}

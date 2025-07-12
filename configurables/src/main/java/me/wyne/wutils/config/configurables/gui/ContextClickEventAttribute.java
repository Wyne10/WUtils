package me.wyne.wutils.config.configurables.gui;

import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ContextClickEventAttribute extends ClickEventAttribute {
    default void apply(InventoryClickEvent event) {
        apply(event, ItemAttributeContext.EMPTY);
    }

    void apply(InventoryClickEvent event, ItemAttributeContext context);
}

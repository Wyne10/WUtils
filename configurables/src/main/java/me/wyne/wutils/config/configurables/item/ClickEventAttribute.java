package me.wyne.wutils.config.configurables.item;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickEventAttribute {
    void apply(InventoryClickEvent event);
}

package me.wyne.wutils.config.configurables.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickEventAttribute {
    void apply(InventoryClickEvent event);
}

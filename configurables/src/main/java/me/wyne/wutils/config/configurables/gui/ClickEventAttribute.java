package me.wyne.wutils.config.configurables.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Reacts to a click on a {@code GuiConfigurable} item. {@code GuiConfigurable}'s click handler
 * runs every registered attribute implementing this interface, in registration order, but does
 * not cancel the event itself — cancelling clicks is triumph-gui's job at the GUI level, or the
 * consumer's.
 */
public interface ClickEventAttribute {
    void apply(@NotNull InventoryClickEvent event);
}

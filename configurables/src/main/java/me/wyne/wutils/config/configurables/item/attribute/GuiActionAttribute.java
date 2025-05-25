package me.wyne.wutils.config.configurables.item.attribute;

import dev.triumphteam.gui.components.GuiAction;
import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ClickEventAttribute;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiActionAttribute extends AttributeBase<GuiAction<InventoryClickEvent>> implements ClickEventAttribute {

    public GuiActionAttribute(String key, GuiAction<InventoryClickEvent> value) {
        super(key, value);
    }

    @Override
    public void apply(ItemStack item) {}

    @Override
    public void apply(InventoryClickEvent event) {
        getValue().execute(event);
    }

}

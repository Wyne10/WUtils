package me.wyne.wutils.config.configurables.item.attribute;

import dev.triumphteam.gui.components.GuiAction;
import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.item.ClickEventAttribute;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiActionAttribute extends AttributeBase<GuiAction<InventoryClickEvent>> implements ClickEventAttribute {

    public GuiActionAttribute(String key, GuiAction<InventoryClickEvent> value) {
        super(key, value);
    }

    @Override
    public void apply(InventoryClickEvent event) {
        getValue().execute(event);
    }

}

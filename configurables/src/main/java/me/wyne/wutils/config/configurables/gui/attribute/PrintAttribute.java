package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.gui.ContextClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PrintAttribute extends ConfigurableAttribute<String> implements ContextClickEventAttribute {

    public PrintAttribute(String key, String value) {
        super(key, value);
    }

    public PrintAttribute(String value) {
        super(GuiItemAttribute.PRINT.getKey(), value);
    }

    @Override
    public void apply(InventoryClickEvent event, ItemAttributeContext context) {
        I18n.global.getPlaceholderComponent(I18n.toLocale(context.getPlayer()), context.getPlayer(), getValue(), context.getTextReplacements()).replace(context.getComponentReplacements()).sendMessage(event.getWhoClicked());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public PrintAttribute create(String key, ConfigurationSection config) {
            return new PrintAttribute(key, config.getString(key));
        }
    }

}

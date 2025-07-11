package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.gui.ContextClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.config.configurables.item.ManualAttribute;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CommandAttribute extends ConfigurableAttribute<String> implements ContextClickEventAttribute, ManualAttribute {

    public CommandAttribute(String key, String value) {
        super(key, value);
    }

    public CommandAttribute(String value) {
        super(GuiItemAttribute.COMMAND.getKey(), value);
    }

    @Override
    public void apply(ItemAttributeContext context) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), I18n.global.getPlaceholderString(I18n.toLocale(context.getPlayer()), context.getPlayer(), getValue(), context.getTextReplacements()).get());
    }

    @Override
    public void apply(InventoryClickEvent event, ItemAttributeContext context) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), I18n.global.getPlaceholderString(I18n.toLocale(event.getWhoClicked()), I18n.toPlayer(event.getWhoClicked()), getValue(), context.getTextReplacements()).get());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public CommandAttribute create(String key, ConfigurationSection config) {
            return new CommandAttribute(key, config.getString(key));
        }
    }

}

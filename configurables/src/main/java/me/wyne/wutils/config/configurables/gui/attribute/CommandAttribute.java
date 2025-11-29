package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
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

import java.util.List;

public class CommandAttribute extends ConfigurableAttribute<List<String>> implements ContextClickEventAttribute, ManualAttribute {

    public CommandAttribute(String key, List<String> value) {
        super(key, value);
    }

    public CommandAttribute(List<String> value) {
        super(GuiItemAttribute.COMMAND.getKey(), value);
    }

    @Override
    public void apply(ItemAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderString(context.getPlayer(), context.getTextReplacements()).get())
                .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    @Override
    public void apply(InventoryClickEvent event, ItemAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderString(event.getWhoClicked(), context.getTextReplacements()).get())
                .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    public static final class Factory implements AttributeFactory<CommandAttribute> {
        @Override
        public CommandAttribute create(String key, ConfigurationSection config) {
            return new CommandAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}

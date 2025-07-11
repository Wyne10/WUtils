package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.config.configurables.gui.ClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ManualAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public class CommandsAttribute extends CompositeAttribute<CommandAttribute> implements ClickEventAttribute, ManualAttribute {

    public CommandsAttribute(String key, Map<String, CommandAttribute> commands) {
        super(key, commands);
    }

    public CommandsAttribute(String key, ConfigurationSection config) {
        super(key, config, new CommandAttribute.Factory());
    }

    public CommandsAttribute(Map<String, CommandAttribute> commands) {
        super(GuiItemAttribute.COMMANDS.getKey(), commands);
    }

    public CommandsAttribute(ConfigurationSection config) {
        super(GuiItemAttribute.COMMANDS.getKey(), config, new CommandAttribute.Factory());
    }

    @Override
    public void apply() {
        getValue().values().forEach(CommandAttribute::apply);
    }

    @Override
    public void apply(ItemAttributeContext context) {
        getValue().values().forEach(attribute -> attribute.apply(context));
    }

    @Override
    public void apply(InventoryClickEvent event) {
        getValue().values().forEach(attribute -> attribute.apply(event));
    }

    @Override
    public void apply(InventoryClickEvent event, ItemAttributeContext context) {
        getValue().values().forEach(attribute -> attribute.apply(event, context));
    }

}


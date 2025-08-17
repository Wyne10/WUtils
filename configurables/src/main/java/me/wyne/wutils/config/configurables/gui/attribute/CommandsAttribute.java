package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import me.wyne.wutils.config.configurables.gui.ContextClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ManualAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

public class CommandsAttribute extends CompositeAttribute<CommandAttribute> implements ContextClickEventAttribute, ManualAttribute {

    public CommandsAttribute(String key, Set<CommandAttribute> commands) {
        super(key, commands);
    }

    public CommandsAttribute(String key, ConfigurationSection config) {
        super(key, config, new CommandAttribute.Factory());
    }

    public CommandsAttribute(Set<CommandAttribute> commands) {
        super(GuiItemAttribute.COMMANDS.getKey(), commands);
    }

    public CommandsAttribute(ConfigurationSection config) {
        super(GuiItemAttribute.COMMANDS.getKey(), config, new CommandAttribute.Factory());
    }

    @Override
    public void apply() {
        getValue().forEach(CommandAttribute::apply);
    }

    @Override
    public void apply(ItemAttributeContext context) {
        getValue().forEach(attribute -> attribute.apply(context));
    }

    @Override
    public void apply(InventoryClickEvent event) {
        getValue().forEach(attribute -> attribute.apply(event));
    }

    @Override
    public void apply(InventoryClickEvent event, ItemAttributeContext context) {
        getValue().forEach(attribute -> attribute.apply(event, context));
    }

}


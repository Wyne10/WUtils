package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.ManualAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public class CommandsAttribute extends CompositeAttribute<CommandAttribute> implements PlayerAwareAttribute, ContextPlaceholderAttribute, ClickEventAttribute, ManualAttribute {

    public CommandsAttribute(String key, Map<String, CommandAttribute> commands) {
        super(key, commands);
    }

    public CommandsAttribute(String key, ConfigurationSection config) {
        super(key, config, new CommandAttribute.Factory());
    }

    public CommandsAttribute(Map<String, CommandAttribute> commands) {
        super(ItemAttribute.COMMANDS.getKey(), commands);
    }

    public CommandsAttribute(ConfigurationSection config) {
        super(ItemAttribute.COMMANDS.getKey(), config, new CommandAttribute.Factory());
    }

    @Override
    public void apply() {
        getValue().values().forEach(CommandAttribute::apply);
    }

    @Override
    public void apply(InventoryClickEvent event) {
        getValue().values().forEach(attribute -> attribute.apply(event));
    }

    @Override
    public void apply(Player player) {
        getValue().values().forEach(attribute -> attribute.apply(player));
    }

    @Override
    public void apply(TextReplacement... replacements) {
        getValue().values().forEach(attribute -> attribute.apply(replacements));
    }

    @Override
    public void apply(ComponentReplacement... replacements) {
        getValue().values().forEach(attribute -> attribute.apply(replacements));
    }

}


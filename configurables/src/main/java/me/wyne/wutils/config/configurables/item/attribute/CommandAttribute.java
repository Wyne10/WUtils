package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ClickEventAttribute;
import me.wyne.wutils.config.configurables.item.ContextPlaceholderAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.PlayerAwareAttribute;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CommandAttribute extends AttributeBase<String> implements ConfigurableAttribute<String>, PlayerAwareAttribute, ContextPlaceholderAttribute, ClickEventAttribute {

    private Player player;
    private TextReplacement[] textReplacements = {};
    private ComponentReplacement[] componentReplacements = {};

    public CommandAttribute(String key, String value) {
        super(key, value);
    }

    public CommandAttribute(String value) {
        super(ItemAttribute.COMMAND.getKey(), value);
    }

    @Override
    public void apply(InventoryClickEvent event) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), I18n.global.getPlaceholderString(I18n.toLocale(player), player, getValue(), textReplacements).get());
    }

    @Override
    public void apply(Player player) {
        this.player = player;
    }

    @Override
    public void apply(TextReplacement... replacements) {
        this.textReplacements = replacements;
    }

    @Override
    public void apply(ComponentReplacement... replacements) {
        this.componentReplacements = replacements;
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new CommandAttribute(config.getString(key));
        }
    }

}

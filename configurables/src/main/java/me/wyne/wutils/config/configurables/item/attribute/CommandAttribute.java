package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.attribute.ManualAttribute;
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

import java.util.Arrays;

public class CommandAttribute extends ConfigurableAttribute<String> implements PlayerAwareAttribute, ContextPlaceholderAttribute, ClickEventAttribute, ManualAttribute {

    private Player player;
    private TextReplacement[] textReplacements = {};

    public CommandAttribute(String key, String value) {
        super(key, value);
    }

    public CommandAttribute(String value) {
        super(ItemAttribute.COMMAND.getKey(), value);
    }

    @Override
    public void apply() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), I18n.global.getPlaceholderString(I18n.toLocale(player), player, getValue(), textReplacements).get());
    }

    @Override
    public void apply(InventoryClickEvent event) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), I18n.global.getPlaceholderString(I18n.toLocale(event.getWhoClicked()), I18n.toPlayer(event.getWhoClicked()), getValue(), textReplacements).get());
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
        this.textReplacements = Arrays.stream(replacements).map(ComponentReplacement::asTextReplacement).toArray(TextReplacement[]::new);
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public CommandAttribute create(String key, ConfigurationSection config) {
            return new CommandAttribute(key, config.getString(key));
        }
    }

}

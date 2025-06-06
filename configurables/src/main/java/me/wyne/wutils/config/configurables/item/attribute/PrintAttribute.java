package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PrintAttribute extends ConfigurableAttribute<String> implements PlayerAwareAttribute, ContextPlaceholderAttribute, ClickEventAttribute {

    private Player player;
    private TextReplacement[] textReplacements = {};
    private ComponentReplacement[] componentReplacements = {};

    public PrintAttribute(String key, String value) {
        super(key, value);
    }

    public PrintAttribute(String value) {
        super(ItemAttribute.PRINT.getKey(), value);
    }

    @Override
    public void apply(InventoryClickEvent event) {
        I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, getValue(), textReplacements).replace(componentReplacements).sendMessage(event.getWhoClicked());
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
        public PrintAttribute create(String key, ConfigurationSection config) {
            return new PrintAttribute(key, config.getString(key));
        }
    }

}

package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class NameAttribute extends AttributeBase<String> implements MetaAttribute, ConfigurableAttribute<String>, PlayerAwareAttribute, ContextPlaceholderAttribute {

    private Player player;
    private TextReplacement[] textReplacements = {};
    private ComponentReplacement[] componentReplacements = {};

    public NameAttribute(String key, String value) {
        super(key, value);
    }

    public NameAttribute(String value) {
        super(ItemAttribute.NAME.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, getValue(), textReplacements).replace(componentReplacements).bungee());
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
            return new NameAttribute(config.getString(key));
        }
    }

}

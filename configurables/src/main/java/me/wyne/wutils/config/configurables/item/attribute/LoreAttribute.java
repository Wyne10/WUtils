package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LoreAttribute extends AttributeBase<List<String>> implements MetaAttribute, ConfigurableAttribute<List<String>>, PlayerAwareAttribute, ContextPlaceholderAttribute {

    private Player player;
    private TextReplacement[] textReplacements = {};
    private ComponentReplacement[] componentReplacements = {};

    public LoreAttribute(List<String> value) {
        super(ItemAttribute.LORE.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.setLoreComponents(getValue().stream().map(s -> I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, s, textReplacements).replace(componentReplacements).bungee()).toList());
    }

    @Override
    public void apply(Player player) {
        this.player = player;
    }

    @Override
    public void apply(ItemStack item, TextReplacement... replacements) {
        this.textReplacements = replacements;
    }

    @Override
    public void apply(ItemStack item, ComponentReplacement... replacements) {
        this.componentReplacements = replacements;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().appendCollection(depth, getKey(), getValue()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new LoreAttribute(config.getStringList(key));
        }
    }

}

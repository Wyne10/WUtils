package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LoreAttribute extends ConfigurableAttribute<List<String>> implements ContextMetaAttribute {

    public LoreAttribute(String key, List<String> value) {
        super(key, value);
    }

    public LoreAttribute(List<String> value) {
        super(ItemAttribute.LORE.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta, ItemAttributeContext context) {
        meta.setLoreComponents(getValue().stream().map(s -> I18n.global.getPlaceholderComponent(I18n.toLocale(context.getPlayer()), context.getPlayer(), s, context.getTextReplacements()).replace(context.getComponentReplacements()).bungee()).toList());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().appendCollection(depth, getKey(), getValue()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public LoreAttribute create(String key, ConfigurationSection config) {
            return new LoreAttribute(key, config.getStringList(key));
        }
    }

}

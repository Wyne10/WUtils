package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

public class NameAttribute extends ConfigurableAttribute<String> implements ContextMetaAttribute {

    public NameAttribute(String key, String value) {
        super(key, value);
    }

    public NameAttribute(String value) {
        super(ItemAttribute.NAME.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta, ItemAttributeContext context) {
        meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(context.getPlayer()), context.getPlayer(), getValue(), context.getTextReplacements()).replace(context.getComponentReplacements()).bungee());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public NameAttribute create(String key, ConfigurationSection config) {
            return new NameAttribute(key, config.getString(key));
        }
    }

}

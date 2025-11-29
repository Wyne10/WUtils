package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.component.BukkitComponentAudiences;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

public class NameAttribute extends ConfigurableAttribute<String> implements ContextMetaAttribute {

    public NameAttribute(String key, String value) {
        super(key, value);
    }

    public NameAttribute(String value) {
        super(ItemAttribute.NAME.getKey(), value);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void apply(ItemMeta meta, ItemAttributeContext context) {
        if (I18n.global.getAudiences() instanceof BukkitComponentAudiences)
            meta.setDisplayNameComponent(I18n.global.accessor(context.getPlayer(), getValue()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).bungee());
        else
            meta.displayName(I18n.global.accessor(context.getPlayer(), getValue()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get());
    }

    public static final class Factory implements AttributeFactory<NameAttribute> {
        @Override
        public NameAttribute create(String key, ConfigurationSection config) {
            return new NameAttribute(key, config.getString(key, ""));
        }
    }

}

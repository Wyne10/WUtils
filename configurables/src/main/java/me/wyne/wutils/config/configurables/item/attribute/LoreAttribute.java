package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.component.BukkitComponentAudiences;
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

    @SuppressWarnings("deprecation")
    @Override
    public void apply(ItemMeta meta, ItemAttributeContext context) {
        if (I18n.global.getAudiences() instanceof BukkitComponentAudiences)
            meta.setLoreComponents(getValue().stream().map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).bungee()).toList());
        else
            meta.lore(getValue().stream().map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get()).toList());
    }

    public static final class Factory implements AttributeFactory<LoreAttribute> {
        @Override
        public LoreAttribute create(String key, ConfigurationSection config) {
            return new LoreAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}

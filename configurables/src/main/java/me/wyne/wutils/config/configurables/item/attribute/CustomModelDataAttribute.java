package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelDataAttribute extends ConfigurableAttribute<Integer> implements MetaAttribute {

    public CustomModelDataAttribute(String key, Integer value) {
        super(key, value);
    }

    public CustomModelDataAttribute(Integer value) {
        super(ItemAttribute.MODEL.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.setCustomModelData(getValue());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public CustomModelDataAttribute create(String key, ConfigurationSection config) {
            return new CustomModelDataAttribute(key, config.contains(key) ? config.getInt(key) : null);
        }
    }

}

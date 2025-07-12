package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.CompositeAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class GenericsAttribute extends CompositeAttribute<GenericAttribute> implements MetaAttribute {

    public GenericsAttribute(String key, Map<String, GenericAttribute> attributes) {
        super(key, attributes);
    }

    public GenericsAttribute(String key, ConfigurationSection config) {
        super(key, config, new GenericAttribute.Factory());
    }

    public GenericsAttribute(Map<String, GenericAttribute> attributes) {
        super(ItemAttribute.ATTRIBUTES.getKey(), attributes);
    }

    public GenericsAttribute(ConfigurationSection config) {
        super(ItemAttribute.ATTRIBUTES.getKey(), config, new GenericAttribute.Factory());
    }

    @Override
    public void apply(ItemMeta meta) {
        getValue().values().forEach(attribute -> attribute.apply(meta));
    }

}


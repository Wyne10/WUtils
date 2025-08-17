package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

public class GenericsAttribute extends CompositeAttribute<GenericAttribute> implements MetaAttribute {

    public GenericsAttribute(String key, Set<GenericAttribute> attributes) {
        super(key, attributes);
    }

    public GenericsAttribute(String key, ConfigurationSection config) {
        super(key, config, new GenericAttribute.Factory());
    }

    public GenericsAttribute(Set<GenericAttribute> attributes) {
        super(ItemAttribute.ATTRIBUTES.getKey(), attributes);
    }

    public GenericsAttribute(ConfigurationSection config) {
        super(ItemAttribute.ATTRIBUTES.getKey(), config, new GenericAttribute.Factory());
    }

    @Override
    public void apply(ItemMeta meta) {
        getValue().forEach(attribute -> attribute.apply(meta));
    }

}

